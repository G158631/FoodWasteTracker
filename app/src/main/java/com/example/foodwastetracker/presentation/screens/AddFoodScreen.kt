package com.example.foodwastetracker.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.data.database.entities.FoodItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    navController: NavController,
    foodRepository: FoodRepository
) {
    var foodName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("pieces") }
    var expirationDays by remember { mutableStateOf("7") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Predefined categories
    val categories = listOf("Fruits", "Vegetables", "Dairy", "Meat", "Pantry", "Frozen", "Other")
    val units = listOf("pieces", "kg", "grams", "liters", "bottles", "packages")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF009688) // Same background as home
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopAppBar(
                title = { Text("Add Food Item", color = Color.White) },
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

            // Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Food Details",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Food Name
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        placeholder = { Text("e.g., Bananas, Milk, Bread") },
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
                            value = category,
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
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quantity and Unit
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
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
                                value = unit,
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
                                            unit = u
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Expiration Days
                    OutlinedTextField(
                        value = expirationDays,
                        onValueChange = { if (it.all { char -> char.isDigit() }) expirationDays = it },
                        label = { Text("Expires in (days)") },
                        placeholder = { Text("7") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    )

                    // Add Button
                    Button(
                        onClick = {
                            if (foodName.isNotBlank() && category.isNotBlank()) {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val expirationDate = System.currentTimeMillis() +
                                                (expirationDays.toIntOrNull() ?: 7) * 24 * 60 * 60 * 1000L

                                        val foodItem = FoodItem(
                                            name = foodName.trim(),
                                            category = category,
                                            quantity = quantity.toIntOrNull() ?: 1,
                                            unit = unit,
                                            expirationDate = expirationDate
                                        )

                                        foodRepository.addFoodItem(foodItem)
                                        navController.navigateUp() // Go back to home
                                    } catch (e: Exception) {
                                        // Handle error
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = foodName.isNotBlank() && category.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Add Food Item", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
