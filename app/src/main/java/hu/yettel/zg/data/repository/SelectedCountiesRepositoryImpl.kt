package hu.yettel.zg.data.repository

import hu.yettel.zg.domain.SelectedCountiesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to store the selected counties between screens
 */
@Singleton
class SelectedCountiesRepositoryImpl
    @Inject
    constructor() : SelectedCountiesRepository {
        private val selectedCountyIds = mutableSetOf<String>()

        override fun addCounty(countyId: String) {
            selectedCountyIds.add(countyId)
        }

        override fun removeCounty(countyId: String) {
            selectedCountyIds.remove(countyId)
        }

        override fun getSelectedCountyIds(): Set<String> = selectedCountyIds.toSet()

        override fun clear() {
            selectedCountyIds.clear()
        }
    }
