package hu.yettel.zg.ui.screens.highway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.model.VehicleCategory
import hu.yettel.zg.domain.model.Vignette
import hu.yettel.zg.domain.model.VignetteTypeEnum
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the Highway screen
 */
sealed interface HighwayUiState {
    val selectedVignetteType: VignetteTypeEnum?

    data object Loading : HighwayUiState {
        override val selectedVignetteType: VignetteTypeEnum? = null
    }

    data class Error(
        val message: String,
    ) : HighwayUiState {
        override val selectedVignetteType: VignetteTypeEnum? = null
    }

    data class Success(
        val vehicle: Vehicle,
        val vignetteTypes: List<VignetteType>,
        val hasYearlyVignette: Boolean,
        override val selectedVignetteType: VignetteTypeEnum?,
    ) : HighwayUiState
}

/**
 * Represents a vignette type with its price and selection state
 */
data class VignetteType(
    val type: VignetteTypeEnum,
    val displayName: String,
    val price: Double,
    val isSelected: Boolean = false,
)

@HiltViewModel
class HighwayViewModel
    @Inject
    constructor(
        private val getHighwayInfoUseCase: GetHighwayInfoUseCase,
        private val getVehicleInfoUseCase: GetVehicleInfoUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HighwayUiState>(HighwayUiState.Loading)
        val uiState: StateFlow<HighwayUiState> = _uiState.asStateFlow()

        private var vehicle: Vehicle? = null
        private var highwayInfo: HighwayInfo? = null

        init {
            loadData()
        }

        fun loadData() {
            _uiState.value = HighwayUiState.Loading
            viewModelScope.launch {
                try {
                    when (val vehicleResult = getVehicleInfoUseCase()) {
                        is Result.Success -> {
                            vehicle = vehicleResult.data

                            getHighwayInfoUseCase().collect { result ->
                                processHighwayInfo(result)
                            }
                        }
                        is Result.Error -> {
                            Timber.e(vehicleResult.exception, "Error loading vehicle info")
                            _uiState.value = HighwayUiState.Error(
                                "Failed to load vehicle info: ${vehicleResult.exception.message}",
                            )
                        }
                        is Result.Loading -> {
                            // Keep the loading state
                        }
                    }
                } catch (
                    @Suppress("TooGenericExceptionCaught") e: Exception,
                ) {
                    Timber.e(e, "Error loading data")
                    _uiState.value = HighwayUiState.Error("Failed to load data: ${e.message}")
                }
            }
        }

        fun selectVignetteType(type: VignetteTypeEnum) {
            val currentState = _uiState.value
            if (currentState is HighwayUiState.Success) {
                val updatedVignetteTypes = currentState.vignetteTypes.map { vignetteType ->
                    vignetteType.copy(isSelected = vignetteType.type == type)
                }
                _uiState.value = currentState.copy(
                    vignetteTypes = updatedVignetteTypes,
                    selectedVignetteType = type,
                )
            }
        }

        private fun processHighwayInfo(result: Result<HighwayInfo>) {
            when (result) {
                is Result.Success -> {
                    highwayInfo = result.data
                    createSuccessState()
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Error loading highway info")
                    _uiState.value = HighwayUiState.Error(
                        "Failed to load highway info: ${result.exception.message}",
                    )
                }
                is Result.Loading -> {
                    _uiState.value = HighwayUiState.Loading
                }
            }
        }

        private fun createSuccessState() {
            val vehicleData = vehicle
            val highwayData = highwayInfo

            if (vehicleData == null || highwayData == null) {
                _uiState.value = HighwayUiState.Error("Missing vehicle or highway data")
                return
            }

            val matchingCategory = highwayData.vehicleCategories.find {
                it.category == vehicleData.type
            }

            if (matchingCategory == null) {
                _uiState.value = HighwayUiState.Error("No matching vehicle category found")
                return
            }

            val matchingVignettes = highwayData.vignettes.filter {
                it.vehicleCategory == vehicleData.type
            }

            val hasYearlyVignette = hasYearlyVignette(matchingVignettes)

            val standardVignetteTypes = createVignetteTypeList(matchingVignettes, matchingCategory)

            _uiState.value = HighwayUiState.Success(
                vehicle = vehicleData,
                vignetteTypes = standardVignetteTypes,
                hasYearlyVignette = hasYearlyVignette,
                selectedVignetteType = standardVignetteTypes.firstOrNull { it.isSelected }?.type,
            )
        }

        private fun createVignetteTypeList(
            vignettes: List<Vignette>,
            category: VehicleCategory,
        ): List<VignetteType> {
            val vignetteTypes = mutableListOf<VignetteType>()

            listOf(VignetteTypeEnum.WEEK, VignetteTypeEnum.MONTH, VignetteTypeEnum.DAY).forEach { typeEnum ->
                val matchingVignette = vignettes.find { vignette ->
                    vignette.types.contains(typeEnum.apiValue)
                }

                matchingVignette?.let {
                    vignetteTypes.add(
                        VignetteType(
                            type = typeEnum,
                            displayName = category.vignetteCategory,
                            price = it.cost,
                            isSelected = typeEnum == VignetteTypeEnum.WEEK,
                        ),
                    )
                }
            }

            return vignetteTypes
        }

        private fun hasYearlyVignette(vignettes: List<Vignette>): Boolean =
            vignettes.any { vignette ->
                vignette.types.any { type ->
                    type == VignetteTypeEnum.YEAR.apiValue || VignetteTypeEnum.isCountyType(type)
                }
            }
    }
