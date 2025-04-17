package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle
import javax.inject.Inject

/**
 * Use case to get information about the user's vehicle
 */
class GetVehicleInfoUseCase
    @Inject
    constructor(
        private val repository: HighwayRepository,
    ) {
        suspend operator fun invoke(): Result<Vehicle> = repository.getVehicleInfo()
    }
