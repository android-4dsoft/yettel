package hu.yettel.zg.domain

import hu.yettel.zg.domain.model.Order
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.OrderResponse
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.usecase.PlaceOrderUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class PlaceOrderUseCaseTest {
    private lateinit var repository: HighwayRepository
    private lateinit var useCase: PlaceOrderUseCase

    @Before
    fun setUp() {
        repository = mock(HighwayRepository::class.java)
        useCase = PlaceOrderUseCase(repository)
    }

    @Test
    fun `invoke returns Success when repository returns success`() =
        runTest {
            // Arrange
            val orderRequest = OrderRequest(
                orders = listOf(
                    Order(
                        type = "DAY",
                        category = "CAR",
                        cost = 5150.0,
                    ),
                ),
            )

            val successResponse = OrderResponse(
                statusCode = "OK",
                receivedOrders = listOf(
                    Order(
                        type = "DAY",
                        category = "CAR",
                        cost = 5150.0,
                    ),
                ),
                message = null,
            )

            `when`(repository.placeOrder(orderRequest)).thenReturn(Result.Success(successResponse))

            // Act
            val result = useCase(orderRequest)

            // Assert
            assertTrue(result is Result.Success)
            assertEquals(successResponse, (result as Result.Success).data)
            verify(repository).placeOrder(orderRequest)
        }

    @Test
    fun `invoke returns Error when repository returns error`() =
        runTest {
            // Arrange
            val orderRequest = OrderRequest(
                orders = listOf(
                    Order(
                        type = "DAY",
                        category = "CAR",
                        cost = 5150.0,
                    ),
                ),
            )

            val exception = RuntimeException("Test exception")
            `when`(repository.placeOrder(orderRequest)).thenReturn(Result.Error(exception))

            // Act
            val result = useCase(orderRequest)

            // Assert
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            verify(repository).placeOrder(orderRequest)
        }
}
