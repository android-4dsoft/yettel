package hu.yettel.zg.data.repository

import hu.yettel.zg.domain.SelectedVignetteRepository
import hu.yettel.zg.domain.model.SelectedVignetteInfo
import hu.yettel.zg.domain.model.VignetteTypeEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedVignetteRepositoryImpl
    @Inject
    constructor() : SelectedVignetteRepository {
        private var selectedVignette: SelectedVignetteInfo? = null

        override fun selectVignetteType(
            type: VignetteTypeEnum,
            category: String,
            price: Double,
        ) {
            selectedVignette = SelectedVignetteInfo(
                type = type,
                category = category,
                price = price,
            )
        }

        override fun getSelectedVignette(): SelectedVignetteInfo? = selectedVignette

        override fun clear() {
            selectedVignette = null
        }
    }
