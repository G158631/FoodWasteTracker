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
import com.example.foodwastetracker.presentation.screens.RecipeCategory

data class RecipesUiState(
    val expiringItems: List<FoodItem> = emptyList(),
    val recipeCategories: List<RecipeCategory> = emptyList(),
    val isLoading: Boolean = false
)

class RecipesViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            combine(
                foodRepository.getExpiringItems(3), // Items expiring in 3 days
                foodRepository.getAllFoodItems()
            ) { expiringItems, allItems ->
                val categories = generateRecipeCategories(allItems, expiringItems)
                RecipesUiState(
                    expiringItems = expiringItems,
                    recipeCategories = categories,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun generateRecipeCategories(
        allItems: List<FoodItem>,
        expiringItems: List<FoodItem>
    ): List<RecipeCategory> {
        val categories = mutableListOf<RecipeCategory>()

        // Get unique categories from user's food items
        val userCategories = allItems.map { it.category }.distinct()

        // Add category-specific recipes
        if (userCategories.contains("Fruits")) {
            categories.add(
                RecipeCategory(
                    name = "Fruit Smoothies & Desserts",
                    description = "Perfect for using ripe fruits before they spoil",
                    emoji = "🍓",
                    recipeCount = 12,
                    avgCookTime = "10 min"
                )
            )
        }

        if (userCategories.contains("Vegetables")) {
            categories.add(
                RecipeCategory(
                    name = "Vegetable Stir-fries",
                    description = "Quick and healthy ways to use fresh vegetables",
                    emoji = "🥬",
                    recipeCount = 18,
                    avgCookTime = "15 min"
                )
            )
        }

        if (userCategories.contains("Dairy")) {
            categories.add(
                RecipeCategory(
                    name = "Cheese & Dairy Dishes",
                    description = "Creamy recipes using milk, cheese, and yogurt",
                    emoji = "🧀",
                    recipeCount = 15,
                    avgCookTime = "20 min"
                )
            )
        }

        // Always add these popular categories
        categories.addAll(listOf(
            RecipeCategory(
                name = "Quick & Easy Meals",
                description = "Simple recipes using common ingredients",
                emoji = "⚡",
                recipeCount = 25,
                avgCookTime = "15 min"
            ),
            RecipeCategory(
                name = "Leftover Makeovers",
                description = "Transform yesterday's food into today's delight",
                emoji = "♻️",
                recipeCount = 20,
                avgCookTime = "12 min"
            ),
            RecipeCategory(
                name = "One-Pot Wonders",
                description = "Complete meals with minimal cleanup",
                emoji = "🍲",
                recipeCount = 16,
                avgCookTime = "25 min"
            ),
            RecipeCategory(
                name = "Healthy Snacks",
                description = "Nutritious bites using fresh ingredients",
                emoji = "🥗",
                recipeCount = 14,
                avgCookTime = "8 min"
            )
        ))

        return categories
    }

    fun refreshRecipes() {
        loadRecipes()
    }
}