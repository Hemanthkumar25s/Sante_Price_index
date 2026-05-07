package com.sante.priceindex.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sante.priceindex.data.model.CommodityPrice
import com.sante.priceindex.data.model.ProfitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

object FirebaseRepository {

    private val database = FirebaseDatabase.getInstance()
    private val pricesRef = database.getReference("mandi_prices")

    // ── Fetch prices from Firebase Realtime Database ──────────────────
    fun getPrices(): Flow<List<CommodityPrice>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val prices = mutableListOf<CommodityPrice>()
                for (child in snapshot.children) {
                    try {
                        val id        = child.key ?: continue
                        val name      = child.child("name").getValue(String::class.java) ?: continue
                        val nameHi    = child.child("name_hi").getValue(String::class.java) ?: ""
                        val emoji     = child.child("emoji").getValue(String::class.java) ?: "🥦"
                        val price     = child.child("price_per_kg").getValue(Double::class.java) ?: 0.0
                        val unit      = child.child("unit").getValue(String::class.java) ?: "kg"
                        val updatedAt = child.child("updated_at").getValue(String::class.java) ?: ""

                        // Parse history_7d array
                        val historySnapshot = child.child("history_7d")
                        val history = mutableListOf<Double>()
                        for (h in historySnapshot.children) {
                            h.getValue(Double::class.java)?.let { history.add(it) }
                        }

                        prices.add(
                            CommodityPrice(
                                id        = id,
                                name      = name,
                                nameHi    = nameHi,
                                emoji     = emoji,
                                pricePerKg = price,
                                unit      = unit,
                                updatedAt = updatedAt,
                                history7d = history
                            )
                        )
                    } catch (e: Exception) {
                        // Skip malformed entries
                    }
                }
                trySend(prices)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        pricesRef.addValueEventListener(listener)
        awaitClose { pricesRef.removeEventListener(listener) }
    }

    // ── Profit calculation (same formula, Firebase-aware) ─────────────
    fun calculateProfit(
        commodity: CommodityPrice,
        quantityKg: Double,
        transportCostTotal: Double,
        wastagePercent: Double,
        profitMarginPercent: Double
    ): ProfitResult {
        val mandiCostTotal = commodity.pricePerKg * quantityKg
        val wastageBuffer  = mandiCostTotal * (wastagePercent / 100.0)
        val totalCost      = mandiCostTotal + transportCostTotal + wastageBuffer
        val costPerKg      = if (quantityKg > 0) totalCost / quantityKg else 0.0
        val rrpPerKg       = costPerKg * (1.0 + profitMarginPercent / 100.0)
        val grossSales     = rrpPerKg * quantityKg
        val netProfit      = grossSales - totalCost

        return ProfitResult(
            mandiCostTotal  = mandiCostTotal,
            transportCost   = transportCostTotal,
            wastageBuffer   = wastageBuffer,
            totalCost       = totalCost,
            costPerKg       = costPerKg,
            rrpPerKg        = rrpPerKg,
            grossSales      = grossSales,
            netProfit       = netProfit,
            marginPercent   = profitMarginPercent
        )
    }
}
