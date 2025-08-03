package com.example.foodwastetracker.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodwastetracker.data.database.entities.FoodItem
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.utils.CameraUtils
import kotlinx.coroutines.launch

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
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showQuantityDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Camera launcher (declare this FIRST)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            photoUri = null
        }
    }

    // Camera permission launcher (declare this SECOND)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, take photo
            val imageFile = CameraUtils.createImageFile(context)
            val uri = CameraUtils.getUriForFile(context, imageFile)
            photoUri = uri
            cameraLauncher.launch(uri)
        } else {
            showPermissionDialog = true
        }
    }

    // Predefined categories and units
    val categories = listOf("Fruits", "Vegetables", "Dairy", "Meat", "Pantry", "Frozen", "Other")
    val units = listOf("pieces", "kg", "grams", "liters", "bottles", "packages")

    // Beautiful gradient background - same as welcome and home screen
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        "Add Food Item",
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

            // Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Food Details",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Photo Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                        // Permission already granted, take photo
                                        val imageFile = CameraUtils.createImageFile(context)
                                        val uri = CameraUtils.getUriForFile(context, imageFile)
                                        photoUri = uri
                                        cameraLauncher.launch(uri)
                                    }
                                    else -> {
                                        // Request permission
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF1F8E9)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Food photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                    error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_gallery)
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add photo",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color(0xFF4CAF50)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "📸 Tap to add photo",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF4CAF50)
                                    )
                                    Text(
                                        text = "(Optional)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                        }
                    }

                    // Food Name
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        placeholder = { Text("e.g., Bananas, Milk, Bread") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            focusedLabelColor = Color(0xFF4CAF50)
                        )
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
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                focusedLabelColor = Color(0xFF4CAF50)
                            )
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
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                focusedLabelColor = Color(0xFF4CAF50)
                            )
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
                                modifier = Modifier.menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    focusedLabelColor = Color(0xFF4CAF50)
                                )
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
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF4CAF50))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),
                            focusedLabelColor = Color(0xFF4CAF50)
                        )
                    )

                    // Add Button
                    Button(
                        onClick = {
                            when {
                                foodName.isBlank() -> {
                                    // Could add name validation popup here if needed
                                }
                                category.isBlank() -> {
                                    // Could add category validation popup here if needed
                                }
                                quantity.toIntOrNull() == 0 || quantity.isBlank() -> {
                                    // Show quantity validation popup
                                    showQuantityDialog = true
                                }
                                else -> {
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
                                                expirationDate = expirationDate,
                                                photoPath = photoUri?.toString()
                                            )

                                            foodRepository.addFoodItem(foodItem)
                                            Toast.makeText(
                                                context,
                                                "✅ ${foodName.trim()} added successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigateUp()
                                        } catch (_: Exception) {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = foodName.isNotBlank() && category.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
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

    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Required") },
            text = { Text("This app needs camera permission to take photos of your food items.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("OK", color = Color(0xFF4CAF50))
                }
            }
        )
    }

    // Quantity Validation Dialog
    if (showQuantityDialog) {
        AlertDialog(
            onDismissRequest = { showQuantityDialog = false },
            title = { Text("Invalid Quantity") },
            text = { Text("Please enter a valid quantity greater than 0. You cannot add food items with zero quantity.") },
            confirmButton = {
                TextButton(
                    onClick = { showQuantityDialog = false }
                ) {
                    Text("OK", color = Color(0xFF4CAF50))
                }
            }
        )
    }
}