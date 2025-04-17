package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.SelectedVignetteRepository
import javax.inject.Inject

class ClearSelectedVignetteUseCase
    @Inject
    constructor(
        private val repository: SelectedVignetteRepository,
    ) {
        operator fun invoke() = repository.clear()
    }
