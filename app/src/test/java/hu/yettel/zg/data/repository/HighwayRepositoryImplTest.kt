package hu.yettel.zg.data.repository

import hu.yettel.zg.data.remote.api.YettelApiService
import hu.yettel.zg.data.remote.model.ApiResponse
import hu.yettel.zg.data.remote.model.County
import hu.yettel.zg.data.remote.model.HighwayInfoPayload
import hu.yettel.zg.data.remote.model.HighwayOrder
import hu.yettel.zg.data.remote.model.HighwayOrderResponse
import hu.yettel.zg.data.remote.model.HighwayVignette
import hu.yettel.zg.data.remote.model.LocalizedText
import hu.yettel.zg.data.remote.model.VehicleCategory
import hu.yettel.zg.data.remote.model.VehicleInfo
import hu.yettel.zg.data.remote.util.NetworkException
import hu.yettel.zg.domain.model.Order
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HighwayRepositoryImplTest {
    private lateinit var apiService: YettelApiService
    private lateinit var repository: HighwayRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        apiService = mock(YettelApiService::class.java)
        repository = HighwayRepositoryImpl(apiService, testDispatcher)
    }

    // ===== getHighwayInfo Tests =====

    @Test
    fun `getHighwayInfo returns success with mapped data when API call succeeds`() =
        runTest {
            // Arrange
            val mockVignette = HighwayVignette(
                vignetteType = listOf("DAY"),
                vehicleCategory = "CAR",
                cost = 5150.0,
                transactionFee = 200.0,
                sum = 5350.0,
            )

            val mockCategory = VehicleCategory(
                category = "CAR",
                vignetteCategory = "D1",
                name = LocalizedText(
                    hu = "Személygépjármű",
                    en = "Car",
                ),
            )

            val mockCounty = County(
                id = "YEAR_11",
                name = "Bács-Kiskun",
            )

            val mockPayload = HighwayInfoPayload(
                highwayVignettes = listOf(mockVignette),
                vehicleCategories = listOf(mockCategory),
                counties = listOf(mockCounty),
            )

            val mockResponse = ApiResponse(
                requestId = "12345678",
                statusCode = "OK",
                payload = mockPayload,
            )

            `when`(apiService.getHighwayInfo()).thenReturn(Response.success(mockResponse))

            // Act
            val result = repository.getHighwayInfo()

            // Assert
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            // Verify domain model mapping
            assertEquals(1, data.vignettes.size)
            assertEquals("DAY", data.vignettes[0].types[0])
            assertEquals("CAR", data.vignettes[0].vehicleCategory)
            assertEquals(5150.0, data.vignettes[0].cost, 0.01)
            assertEquals(200.0, data.vignettes[0].transactionFee, 0.01)
            assertEquals(5350.0, data.vignettes[0].totalCost, 0.01)

            assertEquals(1, data.vehicleCategories.size)
            assertEquals("CAR", data.vehicleCategories[0].category)
            assertEquals("D1", data.vehicleCategories[0].vignetteCategory)
            assertEquals("Személygépjármű", data.vehicleCategories[0].name.hungarian)
            assertEquals("Car", data.vehicleCategories[0].name.english)

            assertEquals(1, data.counties.size)
            assertEquals("YEAR_11", data.counties[0].id)
            assertEquals("Bács-Kiskun", data.counties[0].name)
        }

    @Test
    fun `getHighwayInfo returns error when API call fails with HTTP error`() =
        runTest {
            // Arrange
            val errorResponse = Response.error<ApiResponse<HighwayInfoPayload>>(
                404,
                "Not found".toResponseBody(null),
            )
            `when`(apiService.getHighwayInfo()).thenReturn(errorResponse)

            // Act
            val result = repository.getHighwayInfo()

            // Assert
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error is NetworkException.ApiException)
            assertEquals(404, (error as NetworkException.ApiException).statusCode)
        }

    @Test
    fun `getHighwayInfo returns error when API call returns null body`() =
        runTest {
            // Arrange
            val nullBodyResponse = Response.success<ApiResponse<HighwayInfoPayload>>(null)
            `when`(apiService.getHighwayInfo()).thenReturn(nullBodyResponse)

            // Act
            val result = repository.getHighwayInfo()

            // Assert
            assertTrue(result is Result.Error)
            val exception = (result as Result.Error).exception
            assertEquals("Response body is null", exception.message)
        }

    @Test
    fun `getHighwayInfo returns network error when IOException occurs`() =
        runTest {
            // Arrange
            // Instead of using thenThrow which doesn't work well with suspend functions,
            // we'll return a response that will cause the repository to throw during processing
            `when`(apiService.getHighwayInfo()).thenAnswer {
                throw IOException("Network error")
            }

            // Act
            val result = repository.getHighwayInfo()

            // Assert
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error is NetworkException.NoConnectivityException)
        }

    // ===== getVehicleInfo Tests =====

    @Test
    fun `getVehicleInfo returns success with mapped data when API call succeeds`() =
        runTest {
            // Arrange
            val mockResponse = VehicleInfo(
                requestId = "12345678",
                statusCode = "OK",
                internationalRegistrationCode = "H",
                type = "CAR",
                name = "Michael Scott",
                plate = "abc-123",
                country = LocalizedText(
                    hu = "Magyarország",
                    en = "Hungary",
                ),
                vignetteType = "D1",
            )

            `when`(apiService.getVehicleInfo()).thenReturn(Response.success(mockResponse))

            // Act
            val result = repository.getVehicleInfo()

            // Assert
            assertTrue(result is Result.Success)
            val vehicle = (result as Result.Success).data

            // Verify domain model mapping
            assertEquals("H", vehicle.internationalCode)
            assertEquals("CAR", vehicle.type)
            assertEquals("Michael Scott", vehicle.ownerName)
            assertEquals("abc-123", vehicle.licensePlate)
            assertEquals("Magyarország", vehicle.country.hungarian)
            assertEquals("Hungary", vehicle.country.english)
            assertEquals("D1", vehicle.vignetteType)
        }

    @Test
    fun `getVehicleInfo returns error when API call fails`() =
        runTest {
            // Arrange
            val errorResponse = Response.error<VehicleInfo>(
                500,
                "Server error".toResponseBody(null),
            )
            `when`(apiService.getVehicleInfo()).thenReturn(errorResponse)

            // Act
            val result = repository.getVehicleInfo()

            // Assert
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error is NetworkException.ServerException)
            assertEquals(500, (error as NetworkException.ServerException).statusCode)
        }

    // ===== placeOrder Tests =====

    @Test
    fun `placeOrder correctly maps request and returns response when API call succeeds`() =
        runTest {
            // Arrange
            val domainOrder = Order(
                type = "DAY",
                category = "CAR",
                cost = 5150.0,
            )
            val orderRequest = OrderRequest(listOf(domainOrder))

            val mockResponse = HighwayOrderResponse(
                requestId = "12345678",
                statusCode = "OK",
                receivedOrders = listOf(
                    HighwayOrder(
                        type = "DAY",
                        category = "CAR",
                        cost = 5150.0,
                    ),
                ),
            )

            `when`(apiService.createHighwayOrder(any())).thenReturn(Response.success(mockResponse))

            // Act
            val result = repository.placeOrder(orderRequest)

            // Assert
            assertTrue(result is Result.Success)
            val response = (result as Result.Success).data

            assertEquals("OK", response.statusCode)
            assertEquals(1, response.receivedOrders.size)
            assertEquals("DAY", response.receivedOrders[0].type)
            assertEquals("CAR", response.receivedOrders[0].category)
            assertEquals(5150.0, response.receivedOrders[0].cost, 0.01)

            // Then in your test
            verify(apiService).createHighwayOrder(
                argThat { request ->
                    request.highwayOrders?.size == 1 &&
                        request.highwayOrders?.get(0)?.type == "DAY" &&
                        request.highwayOrders?.get(0)?.category == "CAR" &&
                        request.highwayOrders?.get(0)?.cost == 5150.0
                },
            )
        }

    @Test
    fun `placeOrder returns error when API call fails with bad request`() =
        runTest {
            // Arrange
            val domainOrder = Order(
                type = "DAY",
                category = "CAR",
                cost = 5150.0,
            )
            val orderRequest = OrderRequest(listOf(domainOrder))

            val errorResponse = Response.error<HighwayOrderResponse>(
                400,
                "Invalid request".toResponseBody(null),
            )
            `when`(apiService.createHighwayOrder(any())).thenReturn(errorResponse)

            // Act
            val result = repository.placeOrder(orderRequest)

            // Assert
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error is NetworkException.ApiException)
            assertEquals(400, (error as NetworkException.ApiException).statusCode)
        }

    // Helper function for any() argument matcher
    private fun <T> any(): T = org.mockito.Mockito.any()
}
