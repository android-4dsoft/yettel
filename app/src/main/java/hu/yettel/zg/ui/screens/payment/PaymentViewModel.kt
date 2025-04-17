package hu.yettel.zg.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.yettel.zg.domain.model.Order
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.SelectedVignetteInfo
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.model.VignetteTypeEnum
import hu.yettel.zg.domain.usecase.ClearSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.ClearSelectedVignetteUseCase
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.GetSelectedVignetteUseCase
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
import hu.yettel.zg.domain.usecase.PlaceOrderUseCase
import hu.yettel.zg.ui.screens.vignettes.VignetteCounty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface PaymentUiState {
    data object Loading : PaymentUiState

    data class Error(
        val message: String,
    ) : PaymentUiState

    /**
     * Base class for successful payment states
     */
    sealed class Success(
        open val vehicle: Vehicle,
        open val transactionFee: Double,
        open val totalAmount: Double,
        open val vignetteType: VignetteTypeEnum,
    ) : PaymentUiState

    /**
     * State for county vignettes
     */
    data class CountyVignetteSuccess(
        override val vehicle: Vehicle,
        val selectedVignettes: List<SelectedVignette>,
        override val transactionFee: Double,
        override val totalAmount: Double,
    ) : Success(
            vehicle = vehicle,
            transactionFee = transactionFee,
            totalAmount = totalAmount,
            vignetteType = VignetteTypeEnum.YEAR,
        )

    /**
     * State for country-wide vignettes
     */
    data class CountryVignetteSuccess(
        override val vehicle: Vehicle,
        val selectedVignette: SelectedVignetteInfo,
        override val transactionFee: Double,
        override val totalAmount: Double,
    ) : Success(
            vehicle = vehicle,
            transactionFee = transactionFee,
            totalAmount = totalAmount,
            vignetteType = selectedVignette.type,
        )
}

data class SelectedVignette(
    val county: VignetteCounty,
)

@Suppress("LongParameterList")
@HiltViewModel
class PaymentViewModel
    @Inject
    constructor(
        private val getVehicleInfoUseCase: GetVehicleInfoUseCase,
        private val getHighwayInfoUseCase: GetHighwayInfoUseCase,
        private val getSelectedCountiesUseCase: GetSelectedCountiesUseCase,
        private val getSelectedVignetteUseCase: GetSelectedVignetteUseCase,
        private val clearSelectedCountiesUseCase: ClearSelectedCountiesUseCase,
        private val clearSelectedVignetteUseCase: ClearSelectedVignetteUseCase,
        private val placeOrderUseCase: PlaceOrderUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
        val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

        init {
            loadData()
        }

        @Suppress("LongMethod")
        fun loadData() {
            _uiState.value = PaymentUiState.Loading
            viewModelScope.launch {
                try {
                    // First check if we have a country-wide vignette selection
                    val selectedVignette = getSelectedVignetteUseCase()

                    when (val vehicleResult = getVehicleInfoUseCase()) {
                        is Result.Success -> {
                            val vehicle = vehicleResult.data

                            // Handle country-wide vignette if selected
                            if (selectedVignette != null) {
                                handleCountryVignetteSelection(selectedVignette, vehicle)
                                return@launch
                            }

                            // Otherwise check for county vignettes
                            val selectedCountyIds = getSelectedCountiesUseCase()

                            if (selectedCountyIds.isEmpty()) {
                                _uiState.value = PaymentUiState.Error("No counties or vignette selected")
                                return@launch
                            }

                            getHighwayInfoUseCase().collect { result ->
                                when (result) {
                                    is Result.Success -> {
                                        val highwayInfo = result.data

                                        val countyMap = highwayInfo.counties.associateBy { it.id }

                                        val countyVignettes = highwayInfo.vignettes.filter {
                                            it.vehicleCategory == vehicle.type &&
                                                it.types.any { type ->
                                                    type.startsWith("YEAR_")
                                                }
                                        }

                                        val vignettePrice = countyVignettes.firstOrNull()?.cost ?: 0.0

                                        val selectedVignettes = selectedCountyIds.mapNotNull { countyId ->
                                            val countyInfo = countyMap[countyId]
                                            if (countyInfo != null) {
                                                SelectedVignette(
                                                    county = VignetteCounty(
                                                        id = countyId,
                                                        name = countyInfo.name,
                                                        cost = vignettePrice,
                                                        isSelected = true,
                                                    ),
                                                )
                                            } else {
                                                null
                                            }
                                        }

                                        // Calculate total
                                        val subtotal = selectedVignettes.sumOf { it.county.cost }
                                        val transactionFee = countyVignettes.firstOrNull()?.transactionFee ?: 0.0
                                        val total = subtotal + transactionFee

                                        _uiState.value = PaymentUiState.CountyVignetteSuccess(
                                            vehicle = vehicle,
                                            selectedVignettes = selectedVignettes,
                                            transactionFee = transactionFee,
                                            totalAmount = total,
                                        )
                                    }
                                    is Result.Error -> {
                                        Timber.e(result.exception, "Error loading highway info")
                                        _uiState.value = PaymentUiState.Error(
                                            "Failed to load highway info: ${result.exception.message}",
                                        )
                                    }
                                    is Result.Loading -> {
                                        // Keep loading state
                                    }
                                }
                            }
                        }
                        is Result.Error -> {
                            Timber.e(vehicleResult.exception, "Error loading vehicle info")
                            _uiState.value = PaymentUiState.Error(
                                "Failed to load vehicle info: ${vehicleResult.exception.message}",
                            )
                        }
                        is Result.Loading -> {
                            // Keep loading state
                        }
                    }
                } catch (
                    @Suppress("TooGenericExceptionCaught") e: Exception,
                ) {
                    Timber.e(e, "Error in payment screen")
                    _uiState.value = PaymentUiState.Error("An error occurred: ${e.message}")
                }
            }
        }

        private suspend fun handleCountryVignetteSelection(
            selectedVignette: SelectedVignetteInfo,
            vehicle: Vehicle,
        ) {
            getHighwayInfoUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val highwayInfo = result.data

                        val matchingVignettes = highwayInfo.vignettes.filter {
                            it.vehicleCategory == vehicle.type &&
                                it.types.any { type ->
                                    type == selectedVignette.type.apiValue
                                }
                        }

                        val transactionFee = matchingVignettes.firstOrNull()?.transactionFee ?: 0.0
                        val total = selectedVignette.price + transactionFee

                        _uiState.value = PaymentUiState.CountryVignetteSuccess(
                            vehicle = vehicle,
                            selectedVignette = selectedVignette,
                            transactionFee = transactionFee,
                            totalAmount = total,
                        )
                    }
                    is Result.Error -> {
                        Timber.e(result.exception, "Error loading highway info")
                        _uiState.value = PaymentUiState.Error(
                            "Failed to load highway info: ${result.exception.message}",
                        )
                    }
                    is Result.Loading -> {
                        // Keep loading state
                    }
                }
            }
        }

        suspend fun placeOrder(): Boolean {
            return try {
                val currentState = _uiState.value

                // Create order request based on current state
                val orderRequest = when (currentState) {
                    is PaymentUiState.CountyVignetteSuccess -> {
                        // Create orders for county vignettes
                        val countyOrders = currentState.selectedVignettes.map { selectedVignette ->
                            Order(
                                type = "YEAR_${selectedVignette.county.id.removePrefix("YEAR_")}",
                                category = currentState.vehicle.type,
                                cost = selectedVignette.county.cost,
                            )
                        }

                        OrderRequest(orders = countyOrders)
                    }

                    is PaymentUiState.CountryVignetteSuccess -> {
                        // Create order for country-wide vignette
                        val countryOrder = Order(
                            type = currentState.selectedVignette.type.apiValue,
                            category = currentState.selectedVignette.category,
                            cost = currentState.selectedVignette.price,
                        )

                        OrderRequest(orders = listOf(countryOrder))
                    }

                    else -> {
                        _uiState.value = PaymentUiState.Error("Cannot place order: Invalid state")
                        return false
                    }
                }

                // Place the order
                when (val result = placeOrderUseCase(orderRequest)) {
                    is Result.Success -> {
                        Timber.d("Order placed successfully: ${result.data}")

                        // Restore original state after processing
                        _uiState.value = currentState

                        true
                    }

                    is Result.Error -> {
                        Timber.e(result.exception, "Error placing order")
                        _uiState.value = PaymentUiState.Error(
                            "Failed to place order: ${result.exception.message}",
                        )
                        false
                    }

                    is Result.Loading -> {
                        // Should not happen with the current implementation
                        _uiState.value = currentState
                        false
                    }
                }
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
                Timber.e(e, "Error placing order")
                _uiState.value = PaymentUiState.Error("An error occurred: ${e.message}")
                false
            }
        }

        // Clear selected counties and vignette when payment is successful
        fun clearSelections() {
            clearSelectedCountiesUseCase()
            clearSelectedVignetteUseCase()
        }
    }
