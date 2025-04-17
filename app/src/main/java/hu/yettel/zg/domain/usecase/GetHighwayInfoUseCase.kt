package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case to get information about highway vignettes, vehicle categories, and counties
 */
class GetHighwayInfoUseCase
    @Inject
    constructor(
        private val repository: HighwayRepository,
    ) {
        operator fun invoke(): Flow<Result<HighwayInfo>> =
            flow {
                emit(Result.Loading)
                val result = repository.getHighwayInfo()
                emit(result)
            }
    }
