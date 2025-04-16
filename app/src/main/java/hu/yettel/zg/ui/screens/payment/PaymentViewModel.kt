package hu.yettel.zg.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.usecase.ClearSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
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

    data class Success(
        val vehicle: Vehicle,
        val selectedVignettes: List<SelectedVignette>,
        val transactionFee: Double,
        val totalAmount: Double,
    ) : PaymentUiState
}

data class SelectedVignette(
    val county: VignetteCounty,
)

@HiltViewModel
class PaymentViewModel
    @Inject
    constructor(
        private val getVehicleInfoUseCase: GetVehicleInfoUseCase,
        private val getHighwayInfoUseCase: GetHighwayInfoUseCase,
        private val getSelectedCountiesUseCase: GetSelectedCountiesUseCase,
        private val clearSelectedCountiesUseCase: ClearSelectedCountiesUseCase,
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
                    when (val vehicleResult = getVehicleInfoUseCase()) {
                        is Result.Success -> {
                            val vehicle = vehicleResult.data

                            val selectedCountyIds = getSelectedCountiesUseCase()

                            if (selectedCountyIds.isEmpty()) {
                                _uiState.value = PaymentUiState.Error("No counties selected")
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

                                        _uiState.value = PaymentUiState.Success(
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

        // Clear selected counties when payment is successful
        fun clearSelectedCounties() {
            clearSelectedCountiesUseCase()
        }
    }
