package com.sante.priceindex.data.model

data class CommodityPrice(
    val id: String,
    val name: String,
    val nameHi: String,
    val nameKn: String = "",
    val nameTa: String = "",
    val nameTe: String = "",
    val emoji: String,
    val pricePerKg: Double,
    val unit: String = "kg",
    val updatedAt: String,
    val history7d: List<Double>
) {
    fun getTrend(): Trend {
        if (history7d.size < 7) return Trend.STABLE
        val avg7 = history7d.average()
        val avg3 = history7d.takeLast(3).average()
        val changePct = ((avg3 - avg7) / avg7) * 100
        return when {
            changePct > 5.0  -> Trend.RISING
            changePct < -5.0 -> Trend.FALLING
            else             -> Trend.STABLE
        }
    }

    fun getTrendLabel(): String = when (getTrend()) {
        Trend.RISING  -> "Up"
        Trend.FALLING -> "Down"
        Trend.STABLE  -> "Stable"
    }
}

enum class Trend { RISING, STABLE, FALLING }

data class ProfitResult(
    val mandiCostTotal: Double,
    val transportCost: Double,
    val wastageBuffer: Double,
    val totalCost: Double,
    val costPerKg: Double,
    val rrpPerKg: Double,
    val grossSales: Double,
    val netProfit: Double,
    val marginPercent: Double
)
