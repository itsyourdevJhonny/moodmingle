package com.emc.moodmingle.utils.text

import kotlin.math.floor

object NumberFormatter {
    fun formatValue(value: Long, includeDecimal: Boolean): String {
        return when {
            value >= 1000000000 -> formatBillions(value, includeDecimal)
            value >= 1000000 -> formatMillions(value, includeDecimal)
            value >= 1000 -> formatThousands(value, includeDecimal)
            else -> value.toString()
        }
    }

    private fun formatBillions(value: Long, includeDecimal: Boolean): String {
        val billions = value / 1000000000.0
        return formatWithSuffix(billions, "B", includeDecimal)
    }

    /**
     * Format numbers in millions with or without decimal
     */
    private fun formatMillions(value: Long, includeDecimal: Boolean): String {
        val millions = value / 1000000.0
        return formatWithSuffix(millions, "M", includeDecimal)
    }

    /**
     * Format numbers in thousands with or without decimal
     */
    private fun formatThousands(value: Long, includeDecimal: Boolean): String {
        val thousands = value / 1000.0
        return formatWithSuffix(thousands, "K", includeDecimal)
    }

    /**
     * Format numbers with or without decimal with suffix like "K", "M", "B"
     */
    private fun formatWithSuffix(number: Double, suffix: String, includeDecimal: Boolean): String {
        if (includeDecimal) {
            // Format to one decimal place, without rounding up
//            var formatted = String.format(Locale.US, "%.1f", floor(number * 10) / 10)
            var formatted = "%.1f".format(floor(number * 10) / 10)
            if (formatted.endsWith(".0")) {
                formatted = formatted.substring(0, formatted.length - 2) // Remove ".0" if present
            }
            return formatted + suffix
        } else {
            // Truncate the decimal part entirely
//            return String.format(Locale.US, "%.0f%s", floor(number), suffix)
            return "%.0f%s".format(floor(number), suffix)
        }
    }
}