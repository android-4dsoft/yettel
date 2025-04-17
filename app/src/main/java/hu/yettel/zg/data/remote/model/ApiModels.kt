package hu.yettel.zg.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Common structure
@Serializable
data class ApiResponse<T>(
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("statusCode") val statusCode: String? = null,
    @SerialName("payload") val payload: T? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("dataType") val dataType: String? = null,
)

// Highway Info models
@Serializable
data class HighwayInfoPayload(
    @SerialName("highwayVignettes") val highwayVignettes: List<HighwayVignette>? = null,
    @SerialName("vehicleCategories") val vehicleCategories: List<VehicleCategory>? = null,
    @SerialName("counties") val counties: List<County>? = null,
)

@Serializable
data class HighwayVignette(
    @SerialName("vignetteType") val vignetteType: List<String>? = null,
    @SerialName("vehicleCategory") val vehicleCategory: String? = null,
    @SerialName("cost") val cost: Double? = null,
    @SerialName("trxFee") val transactionFee: Double? = null,
    @SerialName("sum") val sum: Double? = null,
)

@Serializable
data class VehicleCategory(
    @SerialName("category") val category: String? = null,
    @SerialName("vignetteCategory") val vignetteCategory: String? = null,
    @SerialName("name") val name: LocalizedText? = null,
)

@Serializable
data class County(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
)

@Serializable
data class LocalizedText(
    @SerialName("hu") val hu: String? = null,
    @SerialName("en") val en: String? = null,
)

// Vehicle Info model
@Serializable
data class VehicleInfo(
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("statusCode") val statusCode: String? = null,
    @SerialName("internationalRegistrationCode") val internationalRegistrationCode: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("plate") val plate: String? = null,
    @SerialName("country") val country: LocalizedText? = null,
    @SerialName("vignetteType") val vignetteType: String? = null,
)

// Highway Order models
@Serializable
data class HighwayOrder(
    @SerialName("type") val type: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("cost") val cost: Double? = null,
)

@Serializable
data class HighwayOrderRequest(
    @SerialName("highwayOrders") val highwayOrders: List<HighwayOrder>? = null,
)

@Serializable
data class HighwayOrderResponse(
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("statusCode") val statusCode: String? = null,
    @SerialName("receivedOrders") val receivedOrders: List<HighwayOrder>? = null,
    @SerialName("message") val message: String? = null,
)
