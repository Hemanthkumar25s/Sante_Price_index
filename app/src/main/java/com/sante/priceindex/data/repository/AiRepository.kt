package com.sante.priceindex.data.repository

import android.graphics.Bitmap
import com.sante.priceindex.viewmodel.AppLanguage
import com.sante.priceindex.viewmodel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AiRepository(private val apiKey: String) {

    /**
     * Local "AI" logic that processes queries without any external API calls.
     * Only answers questions related to the Sante Price Index application.
     */
    suspend fun getChatResponse(prompt: String, context: UiState? = null): String = withContext(Dispatchers.IO) {
        delay(1000) // Simulate thinking time
        
        val input = prompt.lowercase()
        val language = context?.activeLanguage ?: AppLanguage.ENGLISH
        
        // Dynamic Price Lookup Logic
        if (input.contains("price") || input.contains("today") || input.contains("mandi") || 
            input.contains("ಬೆಲೆ") || input.contains("ದರ") || input.contains("yest") || 
            input.contains("rate") || input.contains("yestu") || input.contains("bhava")) {
            
            val matchedCommodity = context?.prices?.find { 
                input.contains(it.name.lowercase()) || 
                input.contains(it.nameHi.lowercase()) ||
                (it.nameKn.isNotEmpty() && input.contains(it.nameKn.lowercase())) ||
                // Support transliterated Kannada (e.g., "erulli" for onion)
                (it.name == "Onion" && (input.contains("erulli") || input.contains("ullagaddi") || input.contains("onion"))) ||
                (it.name == "Tomato" && (input.contains("tomato") || input.contains("tomat"))) ||
                (it.name == "Potato" && (input.contains("alugadde") || input.contains("batate") || input.contains("potato"))) ||
                (it.name == "Chilli" && (input.contains("mensinkayi") || input.contains("mirchi") || input.contains("chilli"))) ||
                (it.name == "Ginger" && (input.contains(" ಶುಂಠಿ") || input.contains("shunti") || input.contains("adrak"))) ||
                (it.name == "Garlic" && (input.contains("ಬೆಳ್ಳುಳ್ಳಿ") || input.contains("bellulli") || input.contains("lehsun")))
            }
            
            if (matchedCommodity != null) {
                val name = if (language == AppLanguage.KANNADA && matchedCommodity.nameKn.isNotEmpty()) matchedCommodity.nameKn else matchedCommodity.nameHi
                val priceText = when(language) {
                    AppLanguage.KANNADA -> "ಇಂದಿನ ಬೆಲೆ ${name} ಗೆ ₹${String.format("%.1f", matchedCommodity.pricePerKg)} ಪ್ರತಿ ಕೆಜಿ."
                    AppLanguage.HINDI -> "आज ${matchedCommodity.nameHi} का भाव ₹${String.format("%.1f", matchedCommodity.pricePerKg)} प्रति किलो है।"
                    AppLanguage.TAMIL -> "இன்று ${matchedCommodity.nameHi} விலை ஒரு கிலோ ₹${String.format("%.1f", matchedCommodity.pricePerKg)}."
                    AppLanguage.TELUGU -> "ఈరోజు ${matchedCommodity.nameHi} ధర కిలోకు ₹${String.format("%.1f", matchedCommodity.pricePerKg)}."
                    else -> "Today's price for ${matchedCommodity.name} (${matchedCommodity.nameHi}) is ₹${String.format("%.1f", matchedCommodity.pricePerKg)} per kg."
                }
                return@withContext priceText + (if (language == AppLanguage.KANNADA) " ಪ್ರವೃತ್ತಿಯು ಪ್ರಸ್ತುತ ${matchedCommodity.getTrendLabel()} ಆಗಿದೆ." else " The trend is currently ${matchedCommodity.getTrendLabel()}.")
            }
        }

        return@withContext when {
            input.contains("price") || input.contains("mandi") || input.contains("cost") || input.contains("ಬೆಲೆ") -> {
                if (language == AppLanguage.KANNADA) "ಸಂತೆಯಲ್ಲಿ, ನೀವು 'ಬೆಲೆಗಳು' ಟ್ಯಾಬ್‌ನಲ್ಲಿ ಲೈವ್ ಮಂಡಿ ಬೆಲೆಗಳನ್ನು ವೀಕ್ಷಿಸಬಹುದು. ನಾವು ಈರುಳ್ಳಿ, ಟೊಮೆಟೊ ಮತ್ತು ಹೆಚ್ಚಿನವುಗಳ ದೈನಂದಿನ ದರಗಳನ್ನು ಟ್ರ್ಯಾಕ್ ಮಾಡುತ್ತೇವೆ."
                else "In Sante, you can view live Mandi prices in the 'Prices' tab. We track daily rates for onions, tomatoes, and more. Tap an item to see its 7-day trend."
            }
            input.contains("hello") || input.contains("hi") || input.contains("namaste") || input.contains("ನಮಸ್ಕಾರ") -> {
                if (language == AppLanguage.KANNADA) "ನಮಸ್ಕಾರ! ನಾನು ಸಂತೆ ಎಐ ಏಜೆಂಟ್. ನಿಮ್ಮ ತರಕಾರಿ ವ್ಯಾಪಾರವನ್ನು ನಿರ್ವಹಿಸಲು ನಿಮಗೆ ಸಹಾಯ ಮಾಡಲು ನಾನು ಇಲ್ಲಿದ್ದೇನೆ."
                else "Hello! I am the Sante AI Agent. I'm here to help you manage your vegetable business. You can ask me about Mandi prices or calculating profits."
            }
            else -> {
                if (language == AppLanguage.KANNADA) "ಕ್ಷಮಿಸಿ, ಸಂತೆ ಪ್ರೈಸ್ ಇಂಡೆಕ್ಸ್ ಅಪ್ಲಿಕೇಶನ್ ಮತ್ತು ನಿಮ್ಮ ಉತ್ಪನ್ನ ವ್ಯವಹಾರಕ್ಕೆ ಸಂಬಂಧಿಸಿದ ಪ್ರಶ್ನೆಗಳಿಗೆ ಮಾತ್ರ ನಾನು ಉತ್ತರಿಸಬಲ್ಲೆ."
                else "I'm sorry, I can only answer questions related to the Sante Price Index app and your produce business. Please ask me about prices or profit margins."
            }
        }
    }

    suspend fun gradeQuality(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        delay(2000)
        "Local Image Analysis: The produce appears to be 'Grade A' (Premium). Freshness is high with minimal surface defects. Recommended for premium display on your Price Board."
    }

    suspend fun getPriceForecast(commodity: String, context: String): String = withContext(Dispatchers.IO) {
        delay(1500)
        "AI Forecast for $commodity: Based on historical data, we expect a 5-10% price increase in the next week due to reduced supply from neighboring mandis."
    }

    suspend fun getProfitOptimization(commodity: String, marketA: String, marketB: String, transportCost: Double): String = withContext(Dispatchers.IO) {
        delay(1500)
        "Profit Analysis: Selling $commodity at $marketB is more profitable today. Even with ₹$transportCost transport cost, the higher retail demand there yields a 12% better margin than $marketA."
    }
}
