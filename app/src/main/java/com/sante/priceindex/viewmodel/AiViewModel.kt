package com.sante.priceindex.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sante.priceindex.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class AiViewModel(private val repository: AiRepository) : ViewModel() {

    private var currentContext: UiState? = null

    fun updateContext(uiState: UiState) {
        currentContext = uiState
    }

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    init {
        _messages.add(ChatMessage("Hello! I am the Sante AI Agent, your personal assistant for live mandi prices and market insights.", false))
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        _messages.add(ChatMessage(text, true))
        _isLoading.value = true
        viewModelScope.launch {
            val response = repository.getChatResponse(text, currentContext)
            _messages.add(ChatMessage(response, false))
            _isLoading.value = false
        }
    }

    fun analyzeQuality(bitmap: Bitmap) {
        _isLoading.value = true
        viewModelScope.launch {
            _aiResult.value = repository.gradeQuality(bitmap)
            _isLoading.value = false
        }
    }

    fun getPriceForecast(commodity: String, history: String) {
        _isLoading.value = true
        viewModelScope.launch {
            _aiResult.value = repository.getPriceForecast(commodity, history)
            _isLoading.value = false
        }
    }

    fun getProfitOptimization(commodity: String, marketA: String, marketB: String, transportCost: Double) {
        _isLoading.value = true
        viewModelScope.launch {
            _aiResult.value = repository.getProfitOptimization(commodity, marketA, marketB, transportCost)
            _isLoading.value = false
        }
    }

    fun clearResult() {
        _aiResult.value = null
    }
}

class AiViewModelFactory(private val repository: AiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
