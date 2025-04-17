package hu.yettel.zg.domain

import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.OrderResponse
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle

interface HighwayRepository {
    /**
     * Gets information about highway vignettes, vehicle categories, and counties
     */
    suspend fun getHighwayInfo(): Result<HighwayInfo>

    /**
     * Gets information about the user's vehicle
     */
    suspend fun getVehicleInfo(): Result<Vehicle>

    /**
     * Places an order for highway vignettes
     */
    suspend fun placeOrder(orderRequest: OrderRequest): Result<OrderResponse>
}
