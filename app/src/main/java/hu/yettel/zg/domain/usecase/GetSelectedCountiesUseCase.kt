package hu.yettel.zg.domain.usecase

import hu.yettel.zg.domain.SelectedCountiesRepository
import javax.inject.Inject

class GetSelectedCountiesUseCase
    @Inject
    constructor(
        private val repository: SelectedCountiesRepository,
    ) {
        operator fun invoke(): Set<String> = repository.getSelectedCountyIds()
    }
