package com.example.foodwastetracker.presentation.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.foodwastetracker.data.repository.FoodRepository

data class StatisticsUiState(
    val activeItemsCount: Int = 0,
    val consumedItemsCount: Int = 0,
    val expiringItemsCount: Int = 0,
    val totalItemsCount: Int = 0,
    val isLoading: Boolean = false
)

class StatisticsViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                foodRepository.getActiveItemsCount(),
                foodRepository.getConsumedItemsCount(),
                foodRepository.getExpiringItems(3)
            ) { activeCount, consumedCount, expiringItems ->
                StatisticsUiState(
                    activeItemsCount = activeCount,
                    consumedItemsCount = consumedCount,
                    expiringItemsCount = expiringItems.size,
                    totalItemsCount = activeCount + consumedCount,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

}