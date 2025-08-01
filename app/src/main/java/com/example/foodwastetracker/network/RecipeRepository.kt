package com.example.foodwastetracker.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

class RecipeRepository {

    suspend fun searchRecipes(query: String): List<Recipe> {
        return withContext(Dispatchers.IO) {
            // Simulate network delay
            delay(1000)
            // Always return demo data for now (network-independent)
            getDemoRecipes(query)
        }
    }

    suspend fun findRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        return withContext(Dispatchers.IO) {
            // Simulate network delay
            delay(1000)
            // Always return demo data for now
            getDemoRecipesForIngredients(ingredients)
        }
    }

    // Demo data that always works
    private fun getDemoRecipes(query: String): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                title = "Quick ${query} Stir Fry",
                image = "https://picsum.photos/312/231?random=1",
                readyInMinutes = 15,
                servings = 2,
                summary = "A delicious and quick stir fry using fresh ${query.lowercase()}. Perfect for weeknight dinners!"
            ),
            Recipe(
                id = 2,
                title = "${query} Smoothie Bowl",
                image = "https://picsum.photos/312/231?random=2",
                readyInMinutes = 10,
                servings = 1,
                summary = "Healthy smoothie bowl perfect for breakfast. Packed with nutrients and flavor."
            ),
            Recipe(
                id = 3,
                title = "Roasted ${query}",
                image = "https://picsum.photos/312/231?random=3",
                readyInMinutes = 25,
                servings = 4,
                summary = "Simple roasted ${query.lowercase()} with herbs and spices. A classic preparation method."
            ),
            Recipe(
                id = 4,
                title = "${query} Salad Supreme",
                image = "https://picsum.photos/312/231?random=4",
                readyInMinutes = 8,
                servings = 2,
                summary = "Fresh and crispy salad featuring ${query.lowercase()}. Light and refreshing."
            )
        )
    }

    private fun getDemoRecipesForIngredients(ingredients: List<String>): List<Recipe> {
        val ingredientText = ingredients.take(2).joinToString(" & ")
        return listOf(
            Recipe(
                id = 10,
                title = "$ingredientText Fresh Salad",
                image = "https://picsum.photos/312/231?random=10",
                readyInMinutes = 10,
                servings = 2,
                summary = "Fresh salad using your ${ingredientText.lowercase()}. Perfect for using expiring ingredients!"
            ),
            Recipe(
                id = 11,
                title = "$ingredientText Warming Soup",
                image = "https://picsum.photos/312/231?random=11",
                readyInMinutes = 20,
                servings = 3,
                summary = "Warming soup perfect for using up ingredients. Comfort food at its best."
            ),
            Recipe(
                id = 12,
                title = "$ingredientText Power Bowl",
                image = "https://picsum.photos/312/231?random=12",
                readyInMinutes = 12,
                servings = 1,
                summary = "Nutritious power bowl with ${ingredientText.lowercase()}. Healthy and satisfying."
            )
        )
    }
}