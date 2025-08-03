package com.example.foodwastetracker.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.network.Recipe
import com.example.foodwastetracker.presentation.screens.viewmodels.RecipesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    navController: NavController,
    foodRepository: FoodRepository
) {
    // Create ViewModel
    val viewModel = remember { RecipesViewModel(foodRepository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF009688) // Same background as other screens
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { Text("Recipe Suggestions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Loading delicious recipes...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Finding the perfect recipes for you ✨",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Expiring Items Alert
                    if (uiState.expiringItems.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFE0B2) // Light orange
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "⚠️ Use These Ingredients Soon!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    uiState.expiringItems.take(3).forEach { item ->
                                        Text(
                                            text = "• ${item.name} (expires in ${getDaysUntilExpiration(item.expirationDate)} days)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFBF360C)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Suggested Recipes for Expiring Items
                    item {
                        if (uiState.suggestedRecipes.isNotEmpty()) {
                            val listState = rememberLazyListState()
                            val scope = rememberCoroutineScope()

                            Box {
                                LazyRow(
                                    state = listState,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(horizontal = 48.dp) // Space for buttons
                                ) {
                                    items(uiState.suggestedRecipes) { recipe ->
                                        RecipeCard(
                                            recipe = recipe,
                                            onClick = {
                                                println("Recipe clicked: ${recipe.title}")
                                            }
                                        )
                                    }
                                }

                                // Left arrow button
                                FloatingActionButton(
                                    onClick = {
                                        scope.launch {
                                            val currentIndex = listState.firstVisibleItemIndex
                                            if (currentIndex > 0) {
                                                listState.animateScrollToItem(currentIndex - 1)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .size(40.dp),
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Previous",
                                        tint = Color.White
                                    )
                                }

                                // Right arrow button
                                FloatingActionButton(
                                    onClick = {
                                        scope.launch {
                                            val currentIndex = listState.firstVisibleItemIndex
                                            if (currentIndex < uiState.suggestedRecipes.size - 1) {
                                                listState.animateScrollToItem(currentIndex + 1)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(40.dp),
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Next",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

// For Category Recipes sections
                    uiState.categoryRecipes.forEach { (category, recipes) ->
                        if (recipes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "🍽️ $category Recipes",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            item {
                                val listState = rememberLazyListState()
                                val scope = rememberCoroutineScope()

                                Box {
                                    LazyRow(
                                        state = listState,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(horizontal = 48.dp) // Space for buttons
                                    ) {
                                        items(recipes) { recipe ->
                                            RecipeCard(
                                                recipe = recipe,
                                                onClick = {
                                                    println("Recipe clicked: ${recipe.title}")
                                                }
                                            )
                                        }
                                    }

                                    // Left arrow button
                                    FloatingActionButton(
                                        onClick = {
                                            scope.launch {
                                                val currentIndex = listState.firstVisibleItemIndex
                                                if (currentIndex > 0) {
                                                    listState.animateScrollToItem(currentIndex - 1)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(40.dp),
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    ) {
                                        Icon(
                                            Icons.Default.KeyboardArrowLeft,
                                            contentDescription = "Previous",
                                            tint = Color.White
                                        )
                                    }

                                    // Right arrow button
                                    FloatingActionButton(
                                        onClick = {
                                            scope.launch {
                                                val currentIndex = listState.firstVisibleItemIndex
                                                if (currentIndex < recipes.size - 1) {
                                                    listState.animateScrollToItem(currentIndex + 1)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .size(40.dp),
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    ) {
                                        Icon(
                                            Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Next",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Error handling
                    uiState.error?.let { error ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFCDD2) // Light red
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Unable to load some recipes",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                    Text(
                                        text = "Showing cached recipes instead.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Recipe Image
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            // Recipe Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Delete, // Using available icon as timer
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.readyInMinutes}min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning, // Using available icon as people
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.servings} servings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Summary (if available)
                recipe.summary?.let { summary ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = summary.replace(Regex("<.*?>"), ""), // Remove HTML tags
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Helper function
private fun getDaysUntilExpiration(expirationDate: Long): Int {
    return ((expirationDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
}