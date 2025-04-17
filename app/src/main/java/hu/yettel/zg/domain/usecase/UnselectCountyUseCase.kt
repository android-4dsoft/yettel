package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.SelectedCountiesRepository
import javax.inject.Inject

class UnselectCountyUseCase
    @Inject
    constructor(
        private val repository: SelectedCountiesRepository,
    ) {
        operator fun invoke(countyId: String) = repository.removeCounty(countyId)
    }
