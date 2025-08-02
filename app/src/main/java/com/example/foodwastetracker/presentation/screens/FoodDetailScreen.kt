package com.example.foodwastetracker.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.presentation.screens.viewmodels.FoodDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodRepository: FoodRepository,
    foodItemId: String
) {
    // Create ViewModel
    val viewModel = remember { FoodDetailViewModel(foodRepository, foodItemId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedCategory by remember { mutableStateOf("") }
    var editedQuantity by remember { mutableStateOf("") }
    var editedUnit by remember { mutableStateOf("") }
    var editedExpirationDays by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Initialize editing fields when food item loads
    LaunchedEffect(uiState.foodItem) {
        uiState.foodItem?.let { item ->
            editedName = item.name
            editedCategory = item.category
            editedQuantity = item.quantity.toString()
            editedUnit = item.unit
            val daysUntilExpiration = ((item.expirationDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
            editedExpirationDays = maxOf(daysUntilExpiration, 1).toString()
        }
    }

    val categories = listOf("Fruits", "Vegetables", "Dairy", "Meat", "Pantry", "Frozen", "Other")
    val units = listOf("pieces", "kg", "grams", "liters", "bottles", "packages")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF009688)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Food Item" else "Food Details",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (uiState.foodItem != null && !uiState.foodItem!!.isConsumed) {
                        if (isEditing) {
                            // Save button
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val updatedItem = uiState.foodItem!!.copy(
                                            name = editedName.trim(),
                                            category = editedCategory,
                                            quantity = editedQuantity.toIntOrNull() ?: 1,
                                            unit = editedUnit,
                                            expirationDate = System.currentTimeMillis() +
                                                    (editedExpirationDays.toIntOrNull() ?: 7) * 24 * 60 * 60 * 1000L
                                        )
                                        viewModel.updateFoodItem(updatedItem)
                                        isEditing = false
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Save",
                                    tint = Color.White
                                )
                            }
                        } else {
                            // Edit button
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Edit",
                                    tint = Color.White
                                )
                            }
                        }
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
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                uiState.foodItem?.let { foodItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Status Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (foodItem.isConsumed)
                                    Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (foodItem.isConsumed) "✅ Consumed" else "📦 Active",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (foodItem.isConsumed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                                    )
                                    if (foodItem.isConsumed && foodItem.dateConsumed != null) {
                                        Text(
                                            text = "Consumed on ${formatDate(foodItem.dateConsumed)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }

                                if (!foodItem.isConsumed) {
                                    val daysUntilExpiration = ((foodItem.expirationDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
                                    val isExpiring = daysUntilExpiration <= 3

                                    Text(
                                        text = when {
                                            daysUntilExpiration > 0 -> "$daysUntilExpiration days left"
                                            daysUntilExpiration == 0 -> "Expires today"
                                            else -> "Expired ${-daysUntilExpiration} days ago"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isExpiring) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (isExpiring) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Food Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Food Information",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                if (isEditing) {
                                    // Editing Mode
                                    OutlinedTextField(
                                        value = editedName,
                                        onValueChange = { editedName = it },
                                        label = { Text("Food Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    // Category Dropdown
                                    var categoryExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = categoryExpanded,
                                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = editedCategory,
                                            onValueChange = { },
                                            readOnly = true,
                                            label = { Text("Category") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = categoryExpanded,
                                            onDismissRequest = { categoryExpanded = false }
                                        ) {
                                            categories.forEach { cat ->
                                                DropdownMenuItem(
                                                    text = { Text(cat) },
                                                    onClick = {
                                                        editedCategory = cat
                                                        categoryExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = editedQuantity,
                                            onValueChange = { if (it.all { char -> char.isDigit() }) editedQuantity = it },
                                            label = { Text("Quantity") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )

                                        var unitExpanded by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                            expanded = unitExpanded,
                                            onExpandedChange = { unitExpanded = !unitExpanded },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = editedUnit,
                                                onValueChange = { },
                                                readOnly = true,
                                                label = { Text("Unit") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                                modifier = Modifier.menuAnchor()
                                            )
                                            ExposedDropdownMenu(
                                                expanded = unitExpanded,
                                                onDismissRequest = { unitExpanded = false }
                                            ) {
                                                units.forEach { u ->
                                                    DropdownMenuItem(
                                                        text = { Text(u) },
                                                        onClick = {
                                                            editedUnit = u
                                                            unitExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = editedExpirationDays,
                                        onValueChange = { if (it.all { char -> char.isDigit() }) editedExpirationDays = it },
                                        label = { Text("Days until expiration") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                } else {
                                    // View Mode
                                    DetailRow("Name", foodItem.name)
                                    DetailRow("Category", foodItem.category)
                                    DetailRow("Quantity", "${foodItem.quantity} ${foodItem.unit}")
                                    DetailRow("Added on", formatDate(foodItem.purchaseDate))
                                    DetailRow("Expires on", formatDate(foodItem.expirationDate))
                                }
                            }
                        }

                        // Action Buttons (only for active items)
                        if (!foodItem.isConsumed) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Actions",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Mark as Consumed Button
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                viewModel.markAsConsumed()
                                                // Navigate back after marking as consumed
                                                navController.navigateUp()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mark as Consumed")
                                    }

                                    // Delete Button
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                viewModel.deleteFoodItem()
                                                // Navigate back after deletion
                                                navController.navigateUp()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Delete Item")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

