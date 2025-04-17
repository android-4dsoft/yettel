package hu.yettel.zg.domain

interface SelectedCountiesRepository {
    fun addCounty(countyId: String)

    fun removeCounty(countyId: String)

    fun getSelectedCountyIds(): Set<String>

    fun clear()
}
