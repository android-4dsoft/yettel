package hu.yettel.zg.domain.model

sealed interface Result<out T> {
    data class Success<T>(
        val data: T,
    ) : Result<T>

    data class Error(
        val exception: Throwable,
    ) : Result<Nothing>

    data object Loading : Result<Nothing>
}
