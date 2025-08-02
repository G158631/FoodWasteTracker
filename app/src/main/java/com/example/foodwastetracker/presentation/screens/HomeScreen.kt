package com.example.foodwastetracker.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodwastetracker.data.database.entities.FoodItem
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.presentation.screens.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    foodRepository: FoodRepository
) {
    // Create ViewModel
    val viewModel = remember { HomeViewModel(foodRepository) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Food Waste Tracker",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stats Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    value = uiState.activeItemsCount.toString(),
                    label = "Active Items"
                )
                StatItem(
                    value = uiState.expiringItems.size.toString(),
                    label = "Expiring Soon"
                )
            }
        }

        // Expiring Items Alert
        if (uiState.expiringItems.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️ ${uiState.expiringItems.size} items expiring soon!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Check your inventory and use them quickly",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.Add,
                text = "Add Food",
                onClick = { navController.navigate("add_food") }
            )
            QuickActionButton(
                icon = Icons.AutoMirrored.Filled.List,
                text = "Recipes",
                onClick = { navController.navigate("recipes") }
            )
            QuickActionButton(
                icon = Icons.Default.Info,
                text = "Stats",
                onClick = { navController.navigate("statistics") }
            )
        }

        // Food Items List
        Text(
            text = "Your Food Items (${uiState.foodItems.size})",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.foodItems.isEmpty()) {
            // Simple Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No food items yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Tap 'Add Food' to get started!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn {
                items(uiState.foodItems) { foodItem ->
                    FoodItemCard(
                        foodItem = foodItem,
                        onItemClick = {
                            navController.navigate("food_detail/${foodItem.id}")
                        },
                        onMarkConsumed = {
                            viewModel.markAsConsumed(foodItem)
                            Toast.makeText(context, "✅ ${foodItem.name} marked as consumed!", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteFoodItem(foodItem)
                            Toast.makeText(context, "🗑️ ${foodItem.name} deleted successfully!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .size(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    onItemClick: () -> Unit,
    onMarkConsumed: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showConsumedDialog by remember { mutableStateOf(false) }

    val daysUntilExpiration = ((foodItem.expirationDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
    val isExpiringSoon = daysUntilExpiration <= 3

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isExpiringSoon && daysUntilExpiration >= 0)
                MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${foodItem.quantity} ${foodItem.unit} • ${foodItem.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = when {
                        daysUntilExpiration > 0 -> "Expires in $daysUntilExpiration days"
                        daysUntilExpiration == 0 -> "Expires today"
                        else -> "Expired ${-daysUntilExpiration} days ago"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isExpiringSoon) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = { showConsumedDialog = true }) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Mark as consumed",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    /* Delete Confirmation Dialog */
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Food Item") },
            text = { Text("Are you sure you want to delete '${foodItem.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Mark as Consumed Confirmation Dialog
    if (showConsumedDialog) {
        AlertDialog(
            onDismissRequest = { showConsumedDialog = false },
            title = { Text("Mark as Consumed") },
            text = { Text("Mark '${foodItem.name}' as consumed? This will move it to your consumed items list.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConsumedDialog = false
                        onMarkConsumed()
                    }
                ) {
                    Text("Mark as Consumed", color = Color(0xFF4CAF50))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConsumedDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}