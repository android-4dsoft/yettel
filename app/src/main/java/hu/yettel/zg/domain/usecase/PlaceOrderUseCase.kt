@file:Suppress("MaxLineLength")

package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.OrderResponse
import hu.yettel.zg.domain.model.Result
import javax.inject.Inject

/**
 * Use case to place an order for highway vignettes
 */
class PlaceOrderUseCase
    @Inject
    constructor(
        private val repository: HighwayRepository,
    ) {
        suspend operator fun invoke(orderRequest: OrderRequest): Result<OrderResponse> = repository.placeOrder(orderRequest)
    }
