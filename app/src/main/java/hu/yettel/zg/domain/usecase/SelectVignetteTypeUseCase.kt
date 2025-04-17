package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.SelectedVignetteRepository
import hu.yettel.zg.domain.model.VignetteTypeEnum
import javax.inject.Inject

class SelectVignetteTypeUseCase
    @Inject
    constructor(
        private val repository: SelectedVignetteRepository,
    ) {
        operator fun invoke(
            type: VignetteTypeEnum,
            category: String,
            price: Double,
        ) = repository.selectVignetteType(type, category, price)
    }
