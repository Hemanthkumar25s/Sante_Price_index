# Presentation Content: Sante Price Index 🛒📉

## 1. Problem Statement & Objectives
**The Problem:**
*   **Price Information Gap:** Small-scale vegetable vendors rely on word-of-mouth for "Mandi" prices, often leading to buying at high rates.
*   **Complex Profit Calculations:** Manual calculation of transport costs, wastage (5-10%), and margins leads to pricing errors and financial loss.
*   **Traditional Record Keeping:** Reliance on physical notebooks makes tracking inventory and historical trends impossible.
*   **Language Barrier:** Existing financial apps are often too complex or only in English.

**Objectives:**
*   Provide real-time market transparency.
*   Automate complex landed-cost calculations.
*   Empower vendors with data-driven pricing tools.
*   Bridge the digital divide with multi-lingual AI support.

---

## 2. Product Flow
1.  **Onboarding:** Secure Login/Sign-In with Google.
2.  **Market Intelligence:** View live daily prices across multiple commodities.
3.  **Profit Optimization:** Select a commodity -> Input quantity/transport/wastage -> App suggests the Recommended Retail Price (RRP).
4.  **Sales Operations:** Push calculated prices directly to the "Price Board."
5.  **Smart Assistance:** Ask the AI Agent for prices or insights in local languages.
6.  **Business Tracking:** Monitor 7-day trends and manage inventory stock levels.

---

## 3. Architecture
*   **Pattern:** MVVM (Model-View-ViewModel) for clean separation of UI and logic.
*   **UI Layer:** Jetpack Compose (Declarative, reactive UI).
*   **Data Layer:** Repository Pattern (Abstracting Firebase and Local Mock data).
*   **Communication:** StateFlow & Kotlin Coroutines for asynchronous, non-blocking updates.
*   **Security:** Firebase Auth + Credential Manager API.

---

## 4. Feature Coverage
*   **Live Price Watch:** Real-time commodity tracking.
*   **Profit Calculator:** Advanced math for "Landed Cost" analysis.
*   **Digital Price Board:** Customer-facing display for the shop.
*   **Inventory Manager:** Stock-left tracking and profit estimation.
*   **Smart Alerts:** Visual indicators for price jumps/drops.
*   **Multi-lingual UI:** Support for English, Hindi, Kannada, Tamil, and Telugu.

---

## 5. Main Model (App Logic)
*   **Decision Engine:** Logic that factors in **Base Price + Transport + (Wastage%) + Margin = Final Price**.
*   **Trend Analysis:** Logic that calculates percentage change over 7 days to categorize trends as Rising, Falling, or Stable.

---

## 6. Data Model
*   **CommodityPrice:** `id, name, nameHi/Kn, emoji, pricePerKg, history7d`.
*   **InventoryItem:** `id, quantity, buyPrice, sellingPrice, stockLeft`.
*   **UiState:** A central state object containing all active business data, loading status, and user profile.

---

## 7. Dashboard & Reports (Outcomes)
*   **Executive Summary:** High-level cards showing "Total Commodities," "Rising Prices," and "Falling Prices."
*   **Price Movement Reports:** 7-day line-graph visualizations of market volatility.
*   **Inventory Reports:** Real-time visibility into which stock is profitable and which is running low.

---

## 8. Smart Support (AI Agent)
*   **NLP Engine:** Custom-built logic to handle transliterated Kannada/Hindi.
*   **Capabilities:**
    *   Query: *"Erulli yestu"* -> Response: *"Onion is ₹22.5/kg today."*
    *   Query: *"Today's trend"* -> Response: *"Prices are rising for Garlic and Ginger."*
*   **Interface:** A conversational chat bubble UI for natural interaction.

---

## 9. User Assurance
*   **Data Integrity:** Reliable Firebase cloud storage prevents data loss.
*   **Security:** No passwords stored locally; uses Google/Firebase secure tokens.
*   **Offline Resilience:** Instant fallback to demo/cached data if the internet is slow, ensuring the vendor can always work.

---

## 10. App Demo Script
1.  **Intro:** "Start with the 1-second splash screen and instant Google Sign-in."
2.  **Market Check:** "Show the Live Prices tab; notice Onions are rising."
3.  **The Calculation:** "Go to Profit Calc. Input 50kg, ₹150 transport. See the app calculate the exact profit margin."
4.  **The Price Board:** "Click 'Push to Board'. Navigate to Price Board to show how customers see the new price."
5.  **AI Magic:** "Open Sante AI. Type 'erulli rate' to show the voice-like response."
6.  **Outro:** "End with the Profile screen showing the saved market location."

---

## 11. Final Outcomes
*   **Reduced Wastage:** Precise calculations prevent over-purchasing.
*   **Increased Margins:** Vendors now know exactly what to charge to cover all costs.
*   **Speed:** Tasks that took 10 minutes (manual math) now take 10 seconds.
*   **Empowerment:** Technology made accessible to non-tech-savvy users via local language support.
