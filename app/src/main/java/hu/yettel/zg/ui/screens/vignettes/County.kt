package hu.yettel.zg.ui.screens.vignettes

import hu.yettel.zg.R

data class County(
    val id: String, // County ID (e.g., "YEAR_11")
    val name: String, // County name (e.g., "Bács-Kiskun")
    val cost: Double = 5450.0, // Cost of vignette for this county
)

val counties = listOf(
    County("BP", "Budapest"),
    County("YEAR_11", "Bács-Kiskun"),
    County("YEAR_12", "Baranya"),
    County("YEAR_13", "Békés"),
    County("YEAR_14", "Borsod-Abaúj-Zemplén"),
    County("YEAR_15", "Csongrád"),
    County("YEAR_16", "Fejér"),
    County("YEAR_17", "Győr-Moson-Sopron"),
    County("YEAR_18", "Hajdú-Bihar"),
    County("YEAR_19", "Heves"),
    County("YEAR_20", "Jász-Nagykun-Szolnok"),
    County("YEAR_21", "Komárom-Esztergom"),
    County("YEAR_22", "Nógrád"),
    County("YEAR_23", "Pest"),
    County("YEAR_24", "Somogy"),
    County("YEAR_25", "Szabolcs-Szatmár-Bereg"),
    County("YEAR_26", "Tolna"),
    County("YEAR_27", "Vas"),
    County("YEAR_28", "Veszprém"),
    County("YEAR_29", "Zala"),
)

/**
 * Get the resource ID for a county's vector drawable
 */
@Suppress("CyclomaticComplexMethod")
fun getCountyVectorRes(countyId: String): Int =
    when (countyId) {
        "YEAR_11" -> R.drawable.year_11
        "YEAR_12" -> R.drawable.year_12
        "YEAR_13" -> R.drawable.year_13
        "YEAR_14" -> R.drawable.year_14
        "YEAR_15" -> R.drawable.year_15
        "YEAR_16" -> R.drawable.year_16
        "YEAR_17" -> R.drawable.year_17
        "YEAR_18" -> R.drawable.year_18
        "YEAR_19" -> R.drawable.year_19
        "YEAR_20" -> R.drawable.year_20
        "YEAR_21" -> R.drawable.year_21
        "YEAR_22" -> R.drawable.year_22
        "YEAR_23" -> R.drawable.year_23
        "YEAR_24" -> R.drawable.year_24
        "YEAR_25" -> R.drawable.year_25
        "YEAR_26" -> R.drawable.year_26
        "YEAR_27" -> R.drawable.year_27
        "YEAR_28" -> R.drawable.year_28
        "YEAR_29" -> R.drawable.year_29
        else -> R.drawable.bp
    }

fun getCountyAdjacencyMap(): Map<String, List<String>> =
    mapOf(
        "YEAR_11" to listOf("YEAR_12", "YEAR_16", "YEAR_26", "YEAR_15", "YEAR_23", "YEAR_20"),
        "YEAR_12" to listOf("YEAR_11", "YEAR_24", "YEAR_26"),
        "YEAR_13" to listOf("YEAR_18", "YEAR_15", "YEAR_20"),
        "YEAR_14" to listOf("YEAR_18", "YEAR_19", "YEAR_20", "YEAR_22", "YEAR_25"),
        "YEAR_15" to listOf("YEAR_11", "YEAR_13", "YEAR_20"),
        "YEAR_16" to listOf("YEAR_11", "YEAR_21", "YEAR_23", "YEAR_24", "YEAR_26", "YEAR_28"),
        "YEAR_17" to listOf("YEAR_21", "YEAR_27", "YEAR_28"),
        "YEAR_18" to listOf("YEAR_13", "YEAR_14", "YEAR_20", "YEAR_25"),
        "YEAR_19" to listOf("YEAR_14", "YEAR_20", "YEAR_22", "YEAR_23"),
        "YEAR_20" to listOf("YEAR_11", "YEAR_13", "YEAR_14", "YEAR_15", "YEAR_18", "YEAR_19", "YEAR_23"),
        "YEAR_21" to listOf("YEAR_16", "YEAR_17", "YEAR_23", "YEAR_28"),
        "YEAR_22" to listOf("YEAR_14", "YEAR_19", "YEAR_23"),
        "YEAR_23" to listOf("YEAR_11", "YEAR_16", "YEAR_19", "YEAR_20", "YEAR_21", "YEAR_22"),
        "YEAR_24" to listOf("YEAR_12", "YEAR_16", "YEAR_26", "YEAR_28", "YEAR_29"),
        "YEAR_25" to listOf("YEAR_14", "YEAR_18"),
        "YEAR_26" to listOf("YEAR_11", "YEAR_12", "YEAR_16", "YEAR_24"),
        "YEAR_27" to listOf("YEAR_17", "YEAR_28", "YEAR_29"),
        "YEAR_28" to listOf("YEAR_16", "YEAR_17", "YEAR_21", "YEAR_24", "YEAR_27", "YEAR_29"),
        "YEAR_29" to listOf("YEAR_24", "YEAR_27", "YEAR_28"),
        "BP" to listOf("YEAR_23"), // Budapest is adjacent to Pest county
    )

fun isCountyAdjacentToSelected(
    countyId: String,
    selectedCountyIds: List<String>,
    adjacencyMap: Map<String, List<String>>,
): Boolean {
    if (selectedCountyIds.isEmpty()) return true

    val adjacentCounties = adjacencyMap[countyId] ?: emptyList()
    return selectedCountyIds.any { selectedId ->
        selectedId == countyId || adjacentCounties.contains(selectedId)
    }
}
