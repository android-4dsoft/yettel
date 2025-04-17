package hu.yettel.zg.domain.model

enum class VignetteTypeEnum(
    val apiValue: String,
) {
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH"),
    YEAR("YEAR"),
    COUNTY("YEAR_"),
    ;

    companion object {
        fun fromApiValue(value: String): VignetteTypeEnum =
            entries.find { it.apiValue == value || value.startsWith(it.apiValue) }
                ?: if (value.startsWith("YEAR_")) COUNTY else DAY

        fun isCountyType(value: String): Boolean = value.startsWith("YEAR_")
    }
}
