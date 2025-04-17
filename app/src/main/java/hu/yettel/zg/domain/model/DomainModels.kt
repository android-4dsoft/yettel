package hu.yettel.zg.domain.model

data class HighwayInfo(
    val vignettes: List<Vignette>,
    val vehicleCategories: List<VehicleCategory>,
    val counties: List<County>,
)

data class Vignette(
    val types: List<String>,
    val vehicleCategory: String,
    val cost: Double,
    val transactionFee: Double,
    val totalCost: Double,
)

data class VehicleCategory(
    val category: String,
    val vignetteCategory: String,
    val name: LocalizedName,
)

data class County(
    val id: String,
    val name: String,
)

data class LocalizedName(
    val hungarian: String,
    val english: String,
)

data class Vehicle(
    val internationalCode: String,
    val type: String,
    val ownerName: String,
    val licensePlate: String,
    val country: LocalizedName,
    val vignetteType: String,
)

data class Order(
    val type: String,
    val category: String,
    val cost: Double,
)

data class OrderRequest(
    val orders: List<Order>,
)

data class OrderResponse(
    val statusCode: String,
    val receivedOrders: List<Order>,
    val message: String?,
)
