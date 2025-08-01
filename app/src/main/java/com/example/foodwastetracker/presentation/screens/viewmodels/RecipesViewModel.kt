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
import com.example.foodwastetracker.network.Recipe
import com.example.foodwastetracker.network.RecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.first

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

    private suspend fun loadRecipesForItems(expiringItems: List<FoodItem>, allItems: List<FoodItem>) {
        try {
            // Get recipes for expiring ingredients
            val expiringIngredients = expiringItems.map { it.name.lowercase() }
            val suggestedRecipes = if (expiringIngredients.isNotEmpty()) {
                recipeRepository.findRecipesByIngredients(expiringIngredients)
            } else {
                emptyList()
            }

            // Get recipes by category
            val categories = allItems.map { it.category }.distinct()
            val categoryRecipes = mutableMapOf<String, List<Recipe>>()

            for (category in categories) {
                val recipes = recipeRepository.searchRecipes(category)
                if (recipes.isNotEmpty()) {
                    categoryRecipes[category] = recipes.take(3) // Limit to 3 per category
                }
            }

            // Add some general categories
            if (!categoryRecipes.containsKey("Quick Meals")) {
                categoryRecipes["Quick Meals"] = recipeRepository.searchRecipes("quick easy meals").take(3)
            }

            if (!categoryRecipes.containsKey("Healthy")) {
                categoryRecipes["Healthy"] = recipeRepository.searchRecipes("healthy recipes").take(3)
            }

            _uiState.value = _uiState.value.copy(
                suggestedRecipes = suggestedRecipes,
                categoryRecipes = categoryRecipes,
                isLoading = false
            )

        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load recipe data: ${e.message}",
                isLoading = false
            )
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val recipes = recipeRepository.searchRecipes(query)
                val updatedCategoryRecipes = _uiState.value.categoryRecipes.toMutableMap()
                updatedCategoryRecipes["Search Results"] = recipes

                _uiState.value = _uiState.value.copy(
                    categoryRecipes = updatedCategoryRecipes,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Search failed: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun refreshRecipes() {
        loadRecipes()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}