package hu.yettel.zg.ui.screens.vignettes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.VignetteTypeEnum
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.SelectCountyUseCase
import hu.yettel.zg.domain.usecase.UnselectCountyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface VignettesUiState {
    data object Loading : VignettesUiState

    data class Error(
        val message: String,
    ) : VignettesUiState

    data class Success(
        val counties: List<VignetteCounty>,
        val selectedCounties: List<VignetteCounty> = emptyList(),
        val totalCost: Double = 0.0,
    ) : VignettesUiState
}

@Suppress("UnusedPrivateProperty")
@HiltViewModel
class VignettesViewModel
    @Inject
    constructor(
        private val getHighwayInfoUseCase: GetHighwayInfoUseCase,
        private val getSelectedCountiesUseCase: GetSelectedCountiesUseCase,
        private val unselectCountyUseCase: UnselectCountyUseCase,
        private val selectCountyUseCase: SelectCountyUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VignettesUiState>(VignettesUiState.Loading)
        val uiState: StateFlow<VignettesUiState> = _uiState.asStateFlow()

        private val vehicleCategory: String = savedStateHandle.get<String>(VignettesRoute.NAV_ARGUMENT) ?: ""

        private var allCounties = listOf<VignetteCounty>()

        init {
            loadData()
        }

        private fun loadData() {
            viewModelScope.launch {
                getHighwayInfoUseCase().collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.value = VignettesUiState.Loading
                        }
                        is Result.Success -> {
                            processHighwayInfo(result.data)
                        }
                        is Result.Error -> {
                            Timber.e(result.exception, "Error loading highway info")
                            _uiState.value = VignettesUiState.Error(
                                "Failed to load county vignettes: ${result.exception.message}",
                            )
                        }
                    }
                }
            }
        }

        private fun processHighwayInfo(highwayInfo: HighwayInfo) {
            try {
                // Find vignettes with YEAR_ prefix matching the vehicle category
                val countyVignettes = highwayInfo.vignettes.filter { vignette ->
                    vignette.vehicleCategory == vehicleCategory &&
                        vignette.types.any { type -> VignetteTypeEnum.isCountyType(type) }
                }

                if (countyVignettes.isEmpty()) {
                    _uiState.value = VignettesUiState.Error("No county vignettes found for $vehicleCategory")
                    return
                }

                // Create a mapping from county ID to name using the counties info
                val countyMap = highwayInfo.counties.associateBy { it.id }

                // Extract county IDs from the vignette types (those starting with YEAR_)
                val countyIds = countyVignettes
                    .flatMap { vignette ->
                        vignette.types.filter { VignetteTypeEnum.isCountyType(it) }
                    }.distinct()

                val previouslySelectedIds = getSelectedCountiesUseCase()

                // Create VignetteCounty objects for each county
                val countyVignettesList = countyIds
                    .mapNotNull { countyId ->
                        val countyInfo = countyMap[countyId]
                        val vignette = countyVignettes.first()

                        if (countyInfo != null) {
                            VignetteCounty(
                                id = countyId,
                                name = countyInfo.name,
                                cost = vignette.cost,
                                isSelected = previouslySelectedIds.contains(countyId),
                            )
                        } else {
                            null
                        }
                    }.sortedBy { it.name }

                // Combine and store all counties
                allCounties = countyVignettesList

                val selectedCounties = countyVignettesList.filter { it.isSelected }
                val totalCost = selectedCounties.sumOf { it.cost }

                _uiState.value = VignettesUiState.Success(
                    counties = allCounties,
                    selectedCounties = selectedCounties,
                    totalCost = totalCost,
                )
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
                Timber.e(e, "Error processing highway info")
                _uiState.value = VignettesUiState.Error("Failed to process county data: ${e.message}")
            }
        }

        @Suppress("ReturnCount")
        fun toggleCountySelection(countyId: String) {
            val currentState = _uiState.value
            if (currentState !is VignettesUiState.Success) return

            val county = allCounties.find { it.id == countyId } ?: return

            val selectedIds = getSelectedCountiesUseCase()

            if (selectedIds.contains(countyId)) {
                unselectCountyUseCase(countyId)
            } else {
                // Check if adjacent before adding (if there are already selections)
                if (selectedIds.isNotEmpty()) {
                    val isAdjacent = isCountyAdjacentToSelected(
                        countyId,
                        selectedIds.toList(),
                        getCountyAdjacencyMap(),
                    )

                    if (!isAdjacent) {
                        // Not adjacent, don't add and notify
                        return
                    }
                }

                selectCountyUseCase(countyId)
            }

            updateSelectionState()
        }

        private fun updateSelectionState() {
            val currentState = _uiState.value as? VignettesUiState.Success ?: return

            val selectedIds = getSelectedCountiesUseCase()

            val updatedCounties = allCounties.map { county ->
                county.copy(isSelected = selectedIds.contains(county.id))
            }

            val selectedCounties = updatedCounties.filter { it.isSelected }
            val totalCost = selectedCounties.sumOf { it.cost }

            _uiState.update {
                VignettesUiState.Success(
                    counties = updatedCounties,
                    selectedCounties = selectedCounties,
                    totalCost = totalCost,
                )
            }
        }

        fun isAnyCountySelected(): Boolean = getSelectedCountiesUseCase().isNotEmpty()
    }
