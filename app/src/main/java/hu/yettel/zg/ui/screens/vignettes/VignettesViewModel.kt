package hu.yettel.zg.ui.screens.vignettes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface VignettesScreenUiState {
    data object Loading : VignettesScreenUiState
}

@Suppress("UnusedPrivateProperty")
@HiltViewModel
class VignettesViewModel
    @Inject
    constructor(
        private val getHighwayInfoUseCase: GetHighwayInfoUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<VignettesScreenUiState>(VignettesScreenUiState.Loading)
        val uiState: StateFlow<VignettesScreenUiState> = _uiState.asStateFlow()

        val category: String = savedStateHandle.get<String>(VignettesRoute.NAV_ARGUMENT) ?: ""
    }
