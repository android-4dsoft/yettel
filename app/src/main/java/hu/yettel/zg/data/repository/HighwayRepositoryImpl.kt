package hu.yettel.zg.data.repository

import hu.yettel.zg.data.mapper.toApiModel
import hu.yettel.zg.data.mapper.toDomainModel
import hu.yettel.zg.data.remote.api.YettelApiService
import hu.yettel.zg.data.remote.util.NetworkException
import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.OrderRequest
import hu.yettel.zg.domain.model.OrderResponse
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerializationException
import org.jetbrains.annotations.VisibleForTesting
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class HighwayRepositoryImpl
    @Inject
    constructor(
        private val apiService: YettelApiService,
        @VisibleForTesting val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) : HighwayRepository {
        override suspend fun getHighwayInfo(): Result<HighwayInfo> =
            try {
                val response = apiService.getHighwayInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body.toDomainModel())
                    } else {
                        Result.Error(Exception("Response body is null"))
                    }
                } else {
                    handleErrorResponse(response.code(), response.message())
                }
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
                handleException(e)
            }

        override suspend fun getVehicleInfo(): Result<Vehicle> =
            try {
                val response = apiService.getVehicleInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body.toDomainModel())
                    } else {
                        Result.Error(Exception("Response body is null"))
                    }
                } else {
                    handleErrorResponse(response.code(), response.message())
                }
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
                handleException(e)
            }

        override suspend fun placeOrder(orderRequest: OrderRequest): Result<OrderResponse> =
            try {
                val apiRequest = orderRequest.toApiModel()
                val response = apiService.createHighwayOrder(apiRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body.toDomainModel())
                    } else {
                        Result.Error(Exception("Response body is null"))
                    }
                } else {
                    handleErrorResponse(response.code(), response.message())
                }
            } catch (
                @Suppress("TooGenericExceptionCaught") e: Exception,
            ) {
                handleException(e)
            }

        private fun handleErrorResponse(
            code: Int,
            message: String,
        ): Result.Error {
            @Suppress("MagicNumber")
            val error = when (code) {
                400 -> NetworkException.ApiException(
                    statusCode = code,
                    message = "Invalid request: $message",
                )
                404 -> NetworkException.ApiException(
                    statusCode = code,
                    message = "Resource not found: $message",
                )
                in 500..599 -> NetworkException.ServerException(
                    statusCode = code,
                    message = "Server error: $message",
                )
                else -> NetworkException.ApiException(
                    statusCode = code,
                    message = message,
                )
            }
            Timber.e("API Error: $code - $message")
            return Result.Error(error)
        }

        private fun handleException(exception: Exception): Result.Error =
            when (exception) {
                is IOException -> {
                    Timber.e(exception, "Network error")
                    Result.Error(NetworkException.NoConnectivityException(cause = exception))
                }
                is SerializationException -> {
                    Timber.e(exception, "Serialization error")
                    Result.Error(NetworkException.DeserializationException(cause = exception))
                }
                is HttpException -> {
                    Timber.e(exception, "HTTP error: ${exception.code()}")
                    Result.Error(
                        NetworkException.ApiException(
                            statusCode = exception.code(),
                            message = exception.message(),
                        ),
                    )
                }
                else -> {
                    Timber.e(exception, "Unknown error")
                    Result.Error(NetworkException.UnknownNetworkException(cause = exception))
                }
            }
    }
