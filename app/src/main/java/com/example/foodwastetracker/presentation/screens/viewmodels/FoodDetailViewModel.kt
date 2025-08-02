package com.example.foodwastetracker.presentation.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.data.database.entities.FoodItem

data class FoodDetailUiState(
    val foodItem: FoodItem? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class FoodDetailViewModel(
    private val foodRepository: FoodRepository,
    private val foodItemId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodDetailUiState())
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    init {
        loadFoodItem()
    }

    private fun loadFoodItem() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val foodItem = foodRepository.getFoodItemById(foodItemId)

                _uiState.value = if (foodItem != null) {
                    _uiState.value.copy(
                        foodItem = foodItem,
                        isLoading = false
                    )
                } else {
                    _uiState.value.copy(
                        error = "Food item not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load food item: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateFoodItem(updatedItem: FoodItem) {
        viewModelScope.launch {
            try {
                foodRepository.updateFoodItem(updatedItem)
                // Reload the item to get the updated version
                loadFoodItem()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update food item: ${e.message}"
                )
            }
        }
    }

    fun markAsConsumed() {
        viewModelScope.launch {
            try {
                _uiState.value.foodItem?.let { item ->
                    foodRepository.markAsConsumed(item.id)
                    // Reload to show updated status
                    loadFoodItem()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to mark item as consumed: ${e.message}"
                )
            }
        }
    }

    fun deleteFoodItem() {
        viewModelScope.launch {
            try {
                _uiState.value.foodItem?.let { item ->
                    foodRepository.deleteFoodItem(item)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete food item: ${e.message}"
                )
            }
        }
    }

}