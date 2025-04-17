package hu.yettel.zg.data.mapper

import hu.yettel.zg.data.remote.model.ApiResponse
import hu.yettel.zg.data.remote.model.County
import hu.yettel.zg.data.remote.model.HighwayInfoPayload
import hu.yettel.zg.data.remote.model.HighwayOrder
import hu.yettel.zg.data.remote.model.HighwayOrderResponse
import hu.yettel.zg.data.remote.model.HighwayVignette
import hu.yettel.zg.data.remote.model.LocalizedText
import hu.yettel.zg.data.remote.model.VehicleCategory
import hu.yettel.zg.data.remote.model.VehicleInfo
import hu.yettel.zg.domain.model.Order
import hu.yettel.zg.domain.model.OrderRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {
    @Test
    fun `API response to domain model mapping for HighwayInfo is correct`() {
        // Arrange
        val apiVignette = HighwayVignette(
            vignetteType = listOf("DAY", "WEEK"),
            vehicleCategory = "CAR",
            cost = 5150.0,
            transactionFee = 200.0,
            sum = 5350.0,
        )

        val apiCategory = VehicleCategory(
            category = "CAR",
            vignetteCategory = "D1",
            name = LocalizedText(
                hu = "Személygépjármű",
                en = "Car",
            ),
        )

        val apiCounty = County(
            id = "YEAR_11",
            name = "Bács-Kiskun",
        )

        val apiPayload = HighwayInfoPayload(
            highwayVignettes = listOf(apiVignette),
            vehicleCategories = listOf(apiCategory),
            counties = listOf(apiCounty),
        )

        val apiResponse = ApiResponse(
            requestId = "12345678",
            statusCode = "OK",
            payload = apiPayload,
        )

        // Act
        val domainModel = apiResponse.toDomainModel()

        // Assert
        // Verify vignettes
        assertEquals(1, domainModel.vignettes.size)
        assertEquals(2, domainModel.vignettes[0].types.size)
        assertEquals("DAY", domainModel.vignettes[0].types[0])
        assertEquals("WEEK", domainModel.vignettes[0].types[1])
        assertEquals("CAR", domainModel.vignettes[0].vehicleCategory)
        assertEquals(5150.0, domainModel.vignettes[0].cost, 0.01)
        assertEquals(200.0, domainModel.vignettes[0].transactionFee, 0.01)
        assertEquals(5350.0, domainModel.vignettes[0].totalCost, 0.01)

        // Verify vehicle categories
        assertEquals(1, domainModel.vehicleCategories.size)
        assertEquals("CAR", domainModel.vehicleCategories[0].category)
        assertEquals("D1", domainModel.vehicleCategories[0].vignetteCategory)
        assertEquals("Személygépjármű", domainModel.vehicleCategories[0].name.hungarian)
        assertEquals("Car", domainModel.vehicleCategories[0].name.english)

        // Verify counties
        assertEquals(1, domainModel.counties.size)
        assertEquals("YEAR_11", domainModel.counties[0].id)
        assertEquals("Bács-Kiskun", domainModel.counties[0].name)
    }

    @Test
    fun `API response to domain model mapping for VehicleInfo is correct`() {
        // Arrange
        val apiVehicleInfo = VehicleInfo(
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

        // Act
        val domainVehicle = apiVehicleInfo.toDomainModel()

        // Assert
        assertEquals("H", domainVehicle.internationalCode)
        assertEquals("CAR", domainVehicle.type)
        assertEquals("Michael Scott", domainVehicle.ownerName)
        assertEquals("abc-123", domainVehicle.licensePlate)
        assertEquals("Magyarország", domainVehicle.country.hungarian)
        assertEquals("Hungary", domainVehicle.country.english)
        assertEquals("D1", domainVehicle.vignetteType)
    }

    @Test
    fun `Domain model to API model mapping for OrderRequest is correct`() {
        // Arrange
        val domainOrders = listOf(
            Order(
                type = "DAY",
                category = "CAR",
                cost = 5150.0,
            ),
            Order(
                type = "WEEK",
                category = "CAR",
                cost = 6400.0,
            ),
        )
        val domainOrderRequest = OrderRequest(orders = domainOrders)

        // Act
        val apiOrderRequest = domainOrderRequest.toApiModel()

        // Assert
        assertEquals(2, apiOrderRequest.highwayOrders?.size)
        assertEquals("DAY", apiOrderRequest.highwayOrders?.get(0)?.type)
        assertEquals("CAR", apiOrderRequest.highwayOrders?.get(0)?.category)
        assertEquals(5150.0, apiOrderRequest.highwayOrders?.get(0)?.cost)
        assertEquals("WEEK", apiOrderRequest.highwayOrders?.get(1)?.type)
        assertEquals("CAR", apiOrderRequest.highwayOrders?.get(1)?.category)
        assertEquals(6400.0, apiOrderRequest.highwayOrders?.get(1)?.cost)
    }

    @Test
    fun `API response to domain model mapping for HighwayOrderResponse is correct`() {
        // Arrange
        val apiResponse = HighwayOrderResponse(
            requestId = "12345678",
            statusCode = "OK",
            receivedOrders = listOf(
                HighwayOrder(
                    type = "DAY",
                    category = "CAR",
                    cost = 5150.0,
                ),
                HighwayOrder(
                    type = "WEEK",
                    category = "CAR",
                    cost = 6400.0,
                ),
            ),
            message = null,
        )

        // Act
        val domainResponse = apiResponse.toDomainModel()

        // Assert
        assertEquals("OK", domainResponse.statusCode)
        assertEquals(2, domainResponse.receivedOrders.size)
        assertEquals("DAY", domainResponse.receivedOrders[0].type)
        assertEquals("CAR", domainResponse.receivedOrders[0].category)
        assertEquals(5150.0, domainResponse.receivedOrders[0].cost, 0.01)
        assertEquals("WEEK", domainResponse.receivedOrders[1].type)
        assertEquals("CAR", domainResponse.receivedOrders[1].category)
        assertEquals(6400.0, domainResponse.receivedOrders[1].cost, 0.01)
        assertEquals(null, domainResponse.message)
    }

    @Test
    fun `Null handling in mappers works correctly`() {
        // Arrange - Create API models with null values
        val nullVignetteType = HighwayVignette(
            vignetteType = null,
            vehicleCategory = null,
            cost = null,
            transactionFee = null,
            sum = null,
        )

        val nullCategory = VehicleCategory(
            category = null,
            vignetteCategory = null,
            name = null,
        )

        val nullCounty = County(
            id = null,
            name = null,
        )

        val apiPayload = HighwayInfoPayload(
            highwayVignettes = listOf(nullVignetteType),
            vehicleCategories = listOf(nullCategory),
            counties = listOf(nullCounty),
        )

        val apiResponse = ApiResponse<HighwayInfoPayload>(
            requestId = null,
            statusCode = null,
            payload = apiPayload,
        )

        // Act
        val domainModel = apiResponse.toDomainModel()

        // Assert - Verify default values are used
        assertEquals(1, domainModel.vignettes.size)
        assertTrue(domainModel.vignettes[0].types.isEmpty())
        assertEquals("", domainModel.vignettes[0].vehicleCategory)
        assertEquals(0.0, domainModel.vignettes[0].cost, 0.01)
        assertEquals(0.0, domainModel.vignettes[0].transactionFee, 0.01)
        assertEquals(0.0, domainModel.vignettes[0].totalCost, 0.01)

        assertEquals(1, domainModel.vehicleCategories.size)
        assertEquals("", domainModel.vehicleCategories[0].category)
        assertEquals("", domainModel.vehicleCategories[0].vignetteCategory)
        assertEquals("", domainModel.vehicleCategories[0].name.hungarian)
        assertEquals("", domainModel.vehicleCategories[0].name.english)

        assertEquals(1, domainModel.counties.size)
        assertEquals("", domainModel.counties[0].id)
        assertEquals("", domainModel.counties[0].name)
    }
}
