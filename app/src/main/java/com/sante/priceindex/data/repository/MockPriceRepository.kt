package com.sante.priceindex.data.repository

import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.ProfitResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Demo data shaped like daily mandi price data.
 * Replace this with data.gov.in/AGMARKNET sync when an API key or backend proxy is available.
 */
object MockPriceRepository {

    private val today: String
        get() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

    private val demoData: List<CommodityPrice>
        get() = listOf(
            CommodityPrice(
                id = "onion",
                name = "Onion",
                nameHi = "Pyaz (प्याज)",
                nameKn = "Eerulli (ಈರುಳ್ಳಿ)",
                nameTa = "Vengayam (வெங்காயம்)",
                nameTe = "Ulligadda (ఉల్లిగడ్డ)",
                emoji = "🧅",
                pricePerKg = 22.50,
                updatedAt = today,
                history7d = listOf(19.0, 20.5, 21.0, 20.0, 22.0, 22.5, 22.5)
            ),
            CommodityPrice(
                id = "tomato",
                name = "Tomato",
                nameHi = "Tamatar (टमाटर)",
                nameKn = "Tomato (ಟೊಮೆಟೊ)",
                nameTa = "Thakkali (தக்காளி)",
                nameTe = "Tamata (టమాటా)",
                emoji = "🍅",
                pricePerKg = 18.00,
                updatedAt = today,
                history7d = listOf(24.0, 22.0, 20.0, 19.5, 18.5, 18.0, 18.0)
            ),
            CommodityPrice(
                id = "potato",
                name = "Potato",
                nameHi = "Aloo (आलू)",
                nameKn = "Alugadde (ಆಲೂಗಡ್ಡೆ)",
                nameTa = "Urulaikizhanggu (உருளைக்கிழங்கு)",
                nameTe = "Bangaladumpa (బంగాళాదుంప)",
                emoji = "🥔",
                pricePerKg = 15.00,
                updatedAt = today,
                history7d = listOf(14.0, 14.5, 15.0, 15.0, 14.5, 15.0, 15.0)
            ),
            CommodityPrice(
                id = "garlic",
                name = "Garlic",
                nameHi = "Lahsun (लहसुन)",
                nameKn = "Bellulli (ಬೆಳ್ಳುಳ್ಳಿ)",
                nameTa = "Poondu (பூண்டு)",
                nameTe = "Vellulli (వెల్లుల్లి)",
                emoji = "🧄",
                pricePerKg = 80.00,
                updatedAt = today,
                history7d = listOf(70.0, 73.0, 75.0, 77.0, 79.0, 80.0, 80.0)
            ),
            CommodityPrice(
                id = "ginger",
                name = "Ginger",
                nameHi = "Adrak (अदरक)",
                nameKn = "Shunti (ಶುಂಠಿ)",
                nameTa = "Inji (இஞ்சி)",
                nameTe = "Allam (అల్లం)",
                emoji = "🫚",
                pricePerKg = 55.00,
                updatedAt = today,
                history7d = listOf(60.0, 58.0, 57.0, 56.0, 55.0, 55.0, 55.0)
            ),
            CommodityPrice(
                id = "green_chilli",
                name = "Green Chilli",
                nameHi = "Hari Mirch (हरी मिर्च)",
                nameKn = "Hasiru Mensinakayi (ಹಸಿರು ಮೆಣಸಿನಕಾಯಿ)",
                nameTa = "Pachai Milagai (பச்சை மிளகாய்)",
                nameTe = "Pachi Mirapakaya (పచ్చి మిరపకాయ)",
                emoji = "🌶️",
                pricePerKg = 40.00,
                updatedAt = today,
                history7d = listOf(35.0, 37.0, 38.0, 39.0, 40.0, 40.0, 40.0)
            ),
            CommodityPrice(
                id = "coriander",
                name = "Coriander",
                nameHi = "Dhaniya (धनिया)",
                nameKn = "Kothambari (ಕೊತ್ತಂಬರಿ)",
                nameTa = "Kothamalli (கொத்தமல்லி)",
                nameTe = "Kothimeera (కొత్తిమీర)",
                emoji = "🌿",
                pricePerKg = 30.00,
                updatedAt = today,
                history7d = listOf(28.0, 29.0, 30.0, 30.0, 30.0, 30.0, 30.0)
            )
        )

    fun getSeedPrices(): List<CommodityPrice> = demoData

    fun getPrices(): Flow<List<CommodityPrice>> = flow {
        delay(500)
        emit(demoData)
    }

    fun calculateProfit(
        commodity: CommodityPrice,
        quantityKg: Double,
        transportCostTotal: Double,
        wastagePercent: Double,
        profitMarginPercent: Double
    ): ProfitResult {
        val mandiCostTotal = commodity.pricePerKg * quantityKg
        val wastageBuffer = mandiCostTotal * (wastagePercent / 100.0)
        val totalCost = mandiCostTotal + transportCostTotal + wastageBuffer
        val costPerKg = if (quantityKg > 0) totalCost / quantityKg else 0.0
        val rrpPerKg = costPerKg * (1.0 + profitMarginPercent / 100.0)
        val grossSales = rrpPerKg * quantityKg
        val netProfit = grossSales - totalCost

        return ProfitResult(
            mandiCostTotal = mandiCostTotal,
            transportCost = transportCostTotal,
            wastageBuffer = wastageBuffer,
            totalCost = totalCost,
            costPerKg = costPerKg,
            rrpPerKg = rrpPerKg,
            grossSales = grossSales,
            netProfit = netProfit,
            marginPercent = profitMarginPercent
        )
    }
}
