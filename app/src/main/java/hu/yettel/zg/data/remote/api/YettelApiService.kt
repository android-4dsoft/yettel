package hu.yettel.zg.data.remote.api

import hu.yettel.zg.data.remote.model.ApiResponse
import hu.yettel.zg.data.remote.model.HighwayInfoPayload
import hu.yettel.zg.data.remote.model.HighwayOrderRequest
import hu.yettel.zg.data.remote.model.HighwayOrderResponse
import hu.yettel.zg.data.remote.model.VehicleInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface YettelApiService {
    @GET("/v1/highway/info")
    suspend fun getHighwayInfo(): Response<ApiResponse<HighwayInfoPayload>>

    @GET("/v1/highway/vehicle")
    suspend fun getVehicleInfo(): Response<VehicleInfo>

    @POST("/v1/highway/order")
    suspend fun createHighwayOrder(
        @Body orderRequest: HighwayOrderRequest,
    ): Response<HighwayOrderResponse>
}
