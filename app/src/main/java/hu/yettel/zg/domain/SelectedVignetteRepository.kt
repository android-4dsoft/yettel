package hu.yettel.zg.domain

import hu.yettel.zg.domain.model.SelectedVignetteInfo
import hu.yettel.zg.domain.model.VignetteTypeEnum

interface SelectedVignetteRepository {
    fun selectVignetteType(
        type: VignetteTypeEnum,
        category: String,
        price: Double,
    )

    fun getSelectedVignette(): SelectedVignetteInfo?

    fun clear()
}
