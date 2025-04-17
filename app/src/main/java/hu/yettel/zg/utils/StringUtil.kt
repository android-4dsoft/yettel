package hu.yettel.zg.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object StringUtil {
    fun formatPrice(value: Double): String {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = ' ' }
        val formatter = DecimalFormat("#,###", symbols)
        return formatter.format(value)
    }
}
