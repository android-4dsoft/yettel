package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.SelectedVignetteRepository
import hu.yettel.zg.domain.model.SelectedVignetteInfo
import javax.inject.Inject

class GetSelectedVignetteUseCase
    @Inject
    constructor(
        private val repository: SelectedVignetteRepository,
    ) {
        operator fun invoke(): SelectedVignetteInfo? = repository.getSelectedVignette()
    }
