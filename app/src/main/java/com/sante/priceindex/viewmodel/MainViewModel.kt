package com.sante.priceindex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.ProfitResult
import com.sante.priceindex.data.model.Trend
import com.sante.priceindex.data.repository.FirebaseRepository
import com.sante.priceindex.data.repository.MockPriceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class PriceBoardItem(
    val name: String,
    val nameHi: String,
    val nameKn: String = "",
    val nameTa: String = "",
    val nameTe: String = "",
    val emoji: String,
    val price: Double,
    val isFresh: Boolean = true
)

data class InventoryItem(
    val id: String,
    val commodityName: String,
    val quantityKg: Double,
    val buyPricePerKg: Double,
    val sellingPricePerKg: Double,
    val soldKg: Double = 0.0,
    val wastageKg: Double = 0.0
) {
    val stockLeftKg: Double
        get() = (quantityKg - soldKg - wastageKg).coerceAtLeast(0.0)

    val estimatedProfit: Double
        get() = (sellingPricePerKg - buyPricePerKg) * stockLeftKg
}

enum class AppRole(val label: String) {
    ADMIN("Admin"),
    VENDOR("Vendor"),
    STAFF("Staff")
}

enum class AppLanguage(val label: String, val nativeName: String) {
    ENGLISH("English", "English"),
    HINDI("Hindi", "हिन्दी"),
    KANNADA("Kannada", "ಕನ್ನಡ"),
    TAMIL("Tamil", "தமிழ்"),
    TELUGU("Telugu", "తెలుగు")
}

data class UiState(
    val isLoading: Boolean = false,
    val prices: List<CommodityPrice> = emptyList(),
    val error: String? = null,
    val selectedCommodityId: String = "onion",
    val quantityKg: String = "50",
    val transportCost: String = "150",
    val wastagePercent: String = "5",
    val profitMargin: String = "20",
    val profitResult: ProfitResult? = null,
    val priceBoardItems: List<PriceBoardItem> = emptyList(),
    val stallName: String = "Raju's Fresh Vegetables",
    val userName: String = "Raju Kumar",
    val userPhone: String = "+91 9876543210",
    val userLocation: String = "K.R. Market, Bengaluru",
    val activeRole: AppRole = AppRole.VENDOR,
    val activeLanguage: AppLanguage = AppLanguage.ENGLISH,
    val inventoryItems: List<InventoryItem> = emptyList(),
    val hasLoadedPrices: Boolean = false,
    val risingCount: Int = 0,
    val fallingCount: Int = 0
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var priceLoadJob: Job? = null

    fun ensurePricesLoaded() {
        val state = _uiState.value
        if (!state.hasLoadedPrices && !state.isLoading) {
            loadPrices()
        }
    }

    fun loadPrices() {
        priceLoadJob?.cancel()
        priceLoadJob = viewModelScope.launch {
            val fallbackPrices = MockPriceRepository.getSeedPrices()
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                prices = if (_uiState.value.prices.isEmpty()) fallbackPrices else _uiState.value.prices,
                priceBoardItems = seedBoardIfEmpty(_uiState.value.priceBoardItems, fallbackPrices),
                risingCount = fallbackPrices.count { it.getTrend() == Trend.RISING },
                fallingCount = fallbackPrices.count { it.getTrend() == Trend.FALLING }
            )
            try {
                val prices = withTimeout(2_500) {
                    FirebaseRepository.getPrices().first()
                }.ifEmpty { fallbackPrices }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    prices = prices,
                    priceBoardItems = seedBoardIfEmpty(_uiState.value.priceBoardItems, prices),
                    hasLoadedPrices = true,
                    error = null,
                    risingCount = prices.count { it.getTrend() == Trend.RISING },
                    fallingCount = prices.count { it.getTrend() == Trend.FALLING }
                )
                recalculate()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    prices = fallbackPrices,
                    priceBoardItems = seedBoardIfEmpty(_uiState.value.priceBoardItems, fallbackPrices),
                    hasLoadedPrices = true,
                    error = "Live prices unavailable. Showing saved demo prices for now.",
                    risingCount = fallbackPrices.count { it.getTrend() == Trend.RISING },
                    fallingCount = fallbackPrices.count { it.getTrend() == Trend.FALLING }
                )
                recalculate()
            }
        }
    }

    fun selectCommodity(id: String) {
        _uiState.value = _uiState.value.copy(selectedCommodityId = id)
        recalculate()
    }

    fun updateQuantity(value: String) {
        _uiState.value = _uiState.value.copy(quantityKg = value)
        recalculate()
    }

    fun updateTransport(value: String) {
        _uiState.value = _uiState.value.copy(transportCost = value)
        recalculate()
    }

    fun updateWastage(value: String) {
        _uiState.value = _uiState.value.copy(wastagePercent = value)
        recalculate()
    }

    fun updateMargin(value: String) {
        _uiState.value = _uiState.value.copy(profitMargin = value)
        recalculate()
    }

    fun updateStallName(name: String) {
        _uiState.value = _uiState.value.copy(stallName = name)
    }

    fun updateRole(role: AppRole) {
        _uiState.value = _uiState.value.copy(activeRole = role)
    }

    fun updateLanguage(language: AppLanguage) {
        _uiState.value = _uiState.value.copy(activeLanguage = language)
    }

    fun updateUserInfo(name: String, shopName: String, phone: String, location: String) {
        _uiState.value = _uiState.value.copy(
            userName = name,
            stallName = shopName,
            userPhone = phone,
            userLocation = location
        )
    }

    fun addMandiItem(name: String, nameHi: String, nameKn: String, price: Double, emoji: String) {
        val newCommodity = CommodityPrice(
            id = name.lowercase().replace(" ", "_"),
            name = name,
            nameHi = nameHi,
            nameKn = nameKn,
            emoji = emoji,
            pricePerKg = price,
            updatedAt = "2026-05-02",
            history7d = listOf(price, price, price, price, price, price, price)
        )
        val updatedPrices = _uiState.value.prices.toMutableList()
        val existingIdx = updatedPrices.indexOfFirst { it.name.lowercase() == name.lowercase() }
        if (existingIdx >= 0) {
            updatedPrices[existingIdx] = newCommodity
        } else {
            updatedPrices.add(newCommodity)
        }
        _uiState.value = _uiState.value.copy(prices = updatedPrices)
        recalculate()
    }

    fun pushToPriceBoard() {
        val state = _uiState.value
        val commodity = state.prices.find { it.id == state.selectedCommodityId } ?: return
        val result = state.profitResult ?: return
        val newItem = PriceBoardItem(
            name  = commodity.name,
            nameHi = commodity.nameHi,
            nameKn = commodity.nameKn,
            nameTa = commodity.nameTa,
            nameTe = commodity.nameTe,
            emoji = commodity.emoji,
            price = result.rrpPerKg,
            isFresh = true
        )
        val existing = state.priceBoardItems.toMutableList()
        val idx = existing.indexOfFirst { it.name == newItem.name }
        if (idx >= 0) existing[idx] = newItem else existing.add(newItem)
        val trimmed = if (existing.size > 8) existing.takeLast(8) else existing
        _uiState.value = state.copy(priceBoardItems = trimmed)
    }

    fun removePriceBoardItem(name: String) {
        val updated = _uiState.value.priceBoardItems.filter { it.name != name }
        _uiState.value = _uiState.value.copy(priceBoardItems = updated)
    }

    fun addInventoryItem(
        commodityId: String,
        quantityKg: Double,
        buyPricePerKg: Double,
        sellingPricePerKg: Double
    ) {
        val state = _uiState.value
        val commodity = state.prices.find { it.id == commodityId } ?: return
        val item = InventoryItem(
            id = "${commodity.id}-${System.currentTimeMillis()}",
            commodityName = commodity.name,
            quantityKg = quantityKg,
            buyPricePerKg = buyPricePerKg,
            sellingPricePerKg = sellingPricePerKg
        )
        _uiState.value = state.copy(inventoryItems = listOf(item) + state.inventoryItems)
    }

    fun removeInventoryItem(id: String) {
        _uiState.value = _uiState.value.copy(
            inventoryItems = _uiState.value.inventoryItems.filterNot { it.id == id }
        )
    }

    private fun recalculate() {
        val state = _uiState.value
        val commodity = state.prices.find { it.id == state.selectedCommodityId } ?: return
        val qty       = state.quantityKg.toDoubleOrNull() ?: return
        val transport = state.transportCost.toDoubleOrNull() ?: return
        val wastage   = state.wastagePercent.toDoubleOrNull() ?: return
        val margin    = state.profitMargin.toDoubleOrNull() ?: return
        if (qty <= 0) return

        val result = FirebaseRepository.calculateProfit(
            commodity           = commodity,
            quantityKg          = qty,
            transportCostTotal  = transport,
            wastagePercent      = wastage,
            profitMarginPercent = margin
        )
        _uiState.value = state.copy(profitResult = result)
    }

    private fun seedBoardIfEmpty(
        currentItems: List<PriceBoardItem>,
        prices: List<CommodityPrice>
    ): List<PriceBoardItem> {
        if (currentItems.isNotEmpty()) return currentItems
        return prices.take(4).map { commodity ->
            PriceBoardItem(
                name = commodity.name,
                nameHi = commodity.nameHi,
                nameKn = commodity.nameKn,
                nameTa = commodity.nameTa,
                nameTe = commodity.nameTe,
                emoji = commodity.emoji,
                price = commodity.pricePerKg * 1.25,
                isFresh = true
            )
        }
    }
}
