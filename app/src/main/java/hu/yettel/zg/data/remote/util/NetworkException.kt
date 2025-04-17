package hu.yettel.zg.data.remote.util

sealed class NetworkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    data class ApiException(
        val statusCode: Int,
        override val message: String,
    ) : NetworkException("API Error $statusCode: $message")

    data class NoConnectivityException(
        override val message: String = "No internet connection available",
        override val cause: Throwable? = null,
    ) : NetworkException(message, cause)

    data class UnknownNetworkException(
        override val message: String = "Unknown network error occurred",
        override val cause: Throwable? = null,
    ) : NetworkException(message, cause)

    data class ServerException(
        val statusCode: Int,
        override val message: String = "Server error occurred: $statusCode",
    ) : NetworkException(message)

    data class DeserializationException(
        override val message: String = "Failed to parse response",
        override val cause: Throwable? = null,
    ) : NetworkException(message, cause)
}
