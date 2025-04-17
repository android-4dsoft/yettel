package hu.yettel.zg.data.mapper

import hu.yettel.zg.data.remote.model.ApiResponse
import hu.yettel.zg.data.remote.model.HighwayInfoPayload
import hu.yettel.zg.data.remote.model.HighwayOrder
import hu.yettel.zg.data.remote.model.HighwayOrderRequest
import hu.yettel.zg.data.remote.model.HighwayOrderResponse
import hu.yettel.zg.data.remote.model.HighwayVignette
import hu.yettel.zg.data.remote.model.LocalizedText
import hu.yettel.zg.data.remote.model.VehicleInfo
import hu.yettel.zg.domain.model.County
import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.LocalizedName
import hu.yettel.zg.domain.model.Order
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.OrderResponse
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.model.VehicleCategory
import hu.yettel.zg.domain.model.Vignette
import hu.yettel.zg.data.remote.model.County as ApiCounty
import hu.yettel.zg.data.remote.model.VehicleCategory as ApiVehicleCategory

/**
 * Maps API response of highway info to domain model
 */
fun ApiResponse<HighwayInfoPayload>.toDomainModel(): HighwayInfo {
    val payloadData = this.payload

    return HighwayInfo(
        vignettes = payloadData?.highwayVignettes?.map { it.toDomainModel() } ?: emptyList(),
        vehicleCategories = payloadData?.vehicleCategories?.map { it.toDomainModel() } ?: emptyList(),
        counties = payloadData?.counties?.map { it.toDomainModel() } ?: emptyList(),
    )
}

fun HighwayVignette.toDomainModel(): Vignette =
    Vignette(
        types = this.vignetteType ?: emptyList(),
        vehicleCategory = this.vehicleCategory ?: "",
        cost = this.cost ?: 0.0,
        transactionFee = this.transactionFee ?: 0.0,
        totalCost = this.sum ?: 0.0,
    )

fun ApiVehicleCategory.toDomainModel(): VehicleCategory =
    VehicleCategory(
        category = this.category ?: "",
        vignetteCategory = this.vignetteCategory ?: "",
        name = this.name.toDomainModel(),
    )

fun ApiCounty.toDomainModel(): County =
    County(
        id = this.id ?: "",
        name = this.name ?: "",
    )

fun LocalizedText?.toDomainModel(): LocalizedName =
    LocalizedName(
        hungarian = this?.hu ?: "",
        english = this?.en ?: "",
    )

/**
 * Maps API vehicle info to domain model
 */
fun VehicleInfo.toDomainModel(): Vehicle =
    Vehicle(
        internationalCode = this.internationalRegistrationCode ?: "",
        type = this.type ?: "",
        ownerName = this.name ?: "",
        licensePlate = this.plate ?: "",
        country = this.country.toDomainModel(),
        vignetteType = this.vignetteType ?: "",
    )

/**
 * Maps domain order request to API request model
 */
fun OrderRequest.toApiModel(): HighwayOrderRequest =
    HighwayOrderRequest(
        highwayOrders = this.orders.map { it.toApiModel() },
    )

fun Order.toApiModel(): HighwayOrder =
    HighwayOrder(
        type = this.type,
        category = this.category,
        cost = this.cost,
    )

/**
 * Maps API order response to domain model
 */
fun HighwayOrderResponse.toDomainModel(): OrderResponse =
    OrderResponse(
        statusCode = this.statusCode ?: "",
        receivedOrders = this.receivedOrders?.map {
            Order(
                type = it.type ?: "",
                category = it.category ?: "",
                cost = it.cost ?: 0.0,
            )
        } ?: emptyList(),
        message = this.message,
    )
