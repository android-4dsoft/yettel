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
import org.mockito.Mockito.times
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

    // Common mock objects
    private lateinit var mockVignette: HighwayVignette
    private lateinit var mockCategory: VehicleCategory
    private lateinit var mockCounty: County
    private lateinit var mockPayload: HighwayInfoPayload
    private lateinit var mockResponse: ApiResponse<HighwayInfoPayload>
    private lateinit var mockVehicleInfo: VehicleInfo

    @Before
    fun setup() {
        apiService = mock(YettelApiService::class.java)
        repository = HighwayRepositoryImpl(apiService)

        // Arrange
        mockVignette = HighwayVignette(
            vignetteType = listOf("DAY"),
            vehicleCategory = "CAR",
            cost = 5150.0,
            transactionFee = 200.0,
            sum = 5350.0,
        )

        mockCategory = VehicleCategory(
            category = "CAR",
            vignetteCategory = "D1",
            name = LocalizedText(
                hu = "Személygépjármű",
                en = "Car",
            ),
        )

        mockCounty = County(
            id = "YEAR_11",
            name = "Bács-Kiskun",
        )

        mockPayload = HighwayInfoPayload(
            highwayVignettes = listOf(mockVignette),
            vehicleCategories = listOf(mockCategory),
            counties = listOf(mockCounty),
        )

        mockResponse = ApiResponse(
            requestId = "12345678",
            statusCode = "OK",
            payload = mockPayload,
        )

        mockVehicleInfo = VehicleInfo(
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
    }

    // ===== getHighwayInfo Tests =====

    @Test
    fun `getHighwayInfo returns success with mapped data when API call succeeds`() =
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
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

    @Test
    fun `getHighwayInfo returns cached data on subsequent calls`() =
        runTest(testDispatcher) {
            // First call - should hit the API
            `when`(apiService.getHighwayInfo()).thenReturn(Response.success(mockResponse))
            val result1 = repository.getHighwayInfo()

            // Second call - should use cache
            // We can verify this by changing the mock to throw an exception - if cache is working,
            // the exception won't be triggered because the API won't be called
            `when`(apiService.getHighwayInfo()).thenAnswer { throw IOException("Network error") }
            val result2 = repository.getHighwayInfo()

            // Assert both calls return the same data successfully
            assertTrue(result1 is Result.Success)
            assertTrue(result2 is Result.Success)
            assertEquals((result1 as Result.Success).data, (result2 as Result.Success).data)

            // Verify the API was only called once
            verify(apiService, times(1)).getHighwayInfo()
        }

    // ===== getVehicleInfo Tests =====

    @Test
    fun `getVehicleInfo returns success with mapped data when API call succeeds`() =
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
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

    @Test
    fun `getVehicleInfo returns cached data on subsequent calls`() =
        runTest(testDispatcher) {
            // First call - should hit the API
            `when`(apiService.getVehicleInfo()).thenReturn(Response.success(mockVehicleInfo))
            val result1 = repository.getVehicleInfo()

            // Second call - should use cache
            // We can verify this by changing the mock to throw an exception - if cache is working,
            // the exception won't be triggered because the API won't be called
            `when`(apiService.getVehicleInfo()).thenAnswer {
                throw IOException("Network error")
            }
            val result2 = repository.getVehicleInfo()

            // Assert both calls return the same data successfully
            assertTrue(result1 is Result.Success)
            assertTrue(result2 is Result.Success)
            assertEquals((result1 as Result.Success).data, (result2 as Result.Success).data)

            // Verify the API was only called once
            verify(apiService, times(1)).getVehicleInfo()
        }

    // ===== placeOrder Tests =====

    @Test
    fun `placeOrder correctly maps request and returns response when API call succeeds`() =
        runTest(testDispatcher) {
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
        runTest(testDispatcher) {
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
