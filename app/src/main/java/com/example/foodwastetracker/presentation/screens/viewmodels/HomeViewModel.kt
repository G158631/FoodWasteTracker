package com.example.foodwastetracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.data.database.entities.FoodItem

data class HomeUiState(
    val foodItems: List<FoodItem> = emptyList(),
    val expiringItems: List<FoodItem> = emptyList(),
    val activeItemsCount: Int = 0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFoodItems()
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                foodRepository.getAllFoodItems(),
                foodRepository.getExpiringItems(3), // Items expiring in 3 days
                foodRepository.getActiveItemsCount()
            ) { allItems, expiringItems, activeCount ->
                HomeUiState(
                    foodItems = allItems,
                    expiringItems = expiringItems,
                    activeItemsCount = activeCount,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun markAsConsumed(foodItem: FoodItem) {
        viewModelScope.launch {
            foodRepository.markAsConsumed(foodItem.id)
        }
    }

    fun deleteFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            foodRepository.deleteFoodItem(foodItem)
        }
    }

    fun refreshData() {
        loadFoodItems()
    }
}