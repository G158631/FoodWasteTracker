package com.example.foodwastetracker.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.presentation.screens.viewmodels.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    foodRepository: FoodRepository
) {
    // Create ViewModel
    val viewModel = remember { StatisticsViewModel(foodRepository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Beautiful gradient background - same as other screens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50), // Green
                        Color(0xFF66BB6A), // Light Green
                        Color(0xFF81C784)  // Lighter Green
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        "Statistics",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overview Cards
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Items",
                        value = uiState.activeItemsCount.toString(),
                        icon = Icons.Default.Add,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Consumed",
                        value = uiState.consumedItemsCount.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Expiring Soon",
                        value = uiState.expiringItemsCount.toString(),
                        icon = Icons.Default.Warning,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Items",
                        value = uiState.totalItemsCount.toString(),
                        icon = Icons.Default.Delete,
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Food Management Progress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val consumptionRate = if (uiState.totalItemsCount > 0) {
                            (uiState.consumedItemsCount.toFloat() / uiState.totalItemsCount.toFloat())
                        } else 0f

                        // Progress Bar
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Consumption Rate",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "${(consumptionRate * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { consumptionRate },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Color(0xFF4CAF50),
                                trackColor = Color(0xFFE8F5E8)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Insights
                        Text(
                            text = "Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val insights = buildList {
                            if (uiState.expiringItemsCount > 0) {
                                add("⚠️ You have ${uiState.expiringItemsCount} items expiring soon. Use them quickly!")
                            }
                            if (uiState.consumedItemsCount > uiState.activeItemsCount) {
                                add("🎉 Great job! You've consumed more items than you currently have active.")
                            }
                            if (uiState.activeItemsCount > 10) {
                                add("📦 You have many active items. Consider using them before buying more.")
                            }
                            if (consumptionRate > 0.7f) {
                                add("✅ Excellent! You're maintaining a good consumption rate.")
                            }
                            if (isEmpty()) {
                                add("📱 Keep tracking your food items to see personalized insights!")
                            }
                        }

                        insights.forEach { insight ->
                            Text(
                                text = insight,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                // Environmental Impact Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "🌍 Environmental Impact",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val estimatedSavings = uiState.consumedItemsCount * 2.5 // $2.50 per item
                        val co2Saved = uiState.consumedItemsCount * 0.5 // 0.5kg CO2 per item

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ImpactItem(
                                value = "$${estimatedSavings.toInt()}",
                                label = "Money Saved",
                                icon = "💰"
                            )
                            ImpactItem(
                                value = "${co2Saved}kg",
                                label = "CO₂ Reduced",
                                icon = "🌱"
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "By consuming your food items instead of wasting them, you're making a positive impact on both your wallet and the environment!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun ImpactItem(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666)
        )
    }
}