package com.example.foodwastetracker.presentation.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodwastetracker.data.database.entities.FoodItem
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.network.Recipe
import com.example.foodwastetracker.network.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RecipesUiState(
    val expiringItems: List<FoodItem> = emptyList(),
    val suggestedRecipes: List<Recipe> = emptyList(),
    val categoryRecipes: Map<String, List<Recipe>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RecipesViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val recipeRepository = RecipeRepository()

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // First get the food items
                val expiringItems = foodRepository.getExpiringItems(3).first()
                val allItems = foodRepository.getAllFoodItems().first()

                // Load demo recipes immediately
                val suggestedRecipes = recipeRepository.findRecipesByIngredients(
                    expiringItems.map { it.name }
                )

                val categoryRecipes = mutableMapOf<String, List<Recipe>>()

                // Add recipes for user's categories
                val userCategories = allItems.map { it.category }.distinct()
                for (category in userCategories) {
                    categoryRecipes[category] = recipeRepository.searchRecipes(category)
                }

                // Always add these popular categories
                categoryRecipes["Quick Meals"] = recipeRepository.searchRecipes("quick meals")
                categoryRecipes["Healthy"] = recipeRepository.searchRecipes("healthy")

                // Update state with all data
                _uiState.value = RecipesUiState(
                    expiringItems = expiringItems,
                    suggestedRecipes = suggestedRecipes,
                    categoryRecipes = categoryRecipes,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                println("RecipesViewModel Error: ${e.message}")

                // Fallback to basic demo data
                val basicRecipes = recipeRepository.searchRecipes("general")
                _uiState.value = RecipesUiState(
                    expiringItems = emptyList(),
                    suggestedRecipes = emptyList(),
                    categoryRecipes = mapOf("Featured Recipes" to basicRecipes),
                    isLoading = false,
                    error = "Using demo recipes"
                )
            }
        }
    }

}