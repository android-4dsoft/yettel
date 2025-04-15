package hu.yettel.zg.data.remote.util

import hu.yettel.zg.domain.model.Result
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("LongMethod")
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.Success(body)
            } else {
                Result.Error(
                    NetworkException.ApiException(
                        statusCode = response.code(),
                        message = "Response body is null",
                    ),
                )
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
            Timber.e("API Error: ${response.code()} - $errorMessage")
            @Suppress("MagicNumber")
            val exception = when (response.code()) {
                400 -> NetworkException.ApiException(
                    statusCode = response.code(),
                    message = "Invalid request: $errorMessage",
                )
                404 -> NetworkException.ApiException(
                    statusCode = response.code(),
                    message = "Resource not found: $errorMessage",
                )
                in 500..599 -> NetworkException.ServerException(
                    statusCode = response.code(),
                    message = "Server error: $errorMessage",
                )
                else -> NetworkException.ApiException(
                    statusCode = response.code(),
                    message = errorMessage,
                )
            }

            Result.Error(exception)
        }
    } catch (e: IOException) {
        Timber.e(e, "Network error")
        val exception = when (e) {
            is UnknownHostException, is SocketTimeoutException -> NetworkException.NoConnectivityException(cause = e)
            else -> NetworkException.UnknownNetworkException(cause = e)
        }
        Result.Error(exception)
    } catch (e: SerializationException) {
        Timber.e(e, "Deserialization error")
        Result.Error(NetworkException.DeserializationException(cause = e))
    } catch (e: HttpException) {
        Timber.e(e, "HTTP error")
        Result.Error(
            NetworkException.ApiException(
                statusCode = e.code(),
                message = e.message(),
            ),
        )
    } catch (
        @Suppress("TooGenericExceptionCaught") e: Exception,
    ) {
        Timber.e(e, "Generic error")
        Result.Error(NetworkException.UnknownNetworkException(cause = e))
    }
