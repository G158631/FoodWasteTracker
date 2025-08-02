package com.example.foodwastetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodwastetracker.data.repository.FoodRepository
import com.example.foodwastetracker.di.DatabaseModule
import com.example.foodwastetracker.presentation.screens.AddFoodScreen
import com.example.foodwastetracker.presentation.screens.FoodDetailScreen
import com.example.foodwastetracker.presentation.screens.HomeScreen
import com.example.foodwastetracker.presentation.screens.RecipesScreen
import com.example.foodwastetracker.presentation.screens.StatisticsScreen
import com.example.foodwastetracker.ui.theme.FoodWasteTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: FoodRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Permission result handled
            }

            // Check and request notification permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Initialize database and repository
        val database = DatabaseModule.provideAppDatabase(this)
        val foodItemDao = DatabaseModule.provideFoodItemDao(database)
        repository = DatabaseModule.provideFoodRepository(foodItemDao)

        // Add test data for demo
        lifecycleScope.launch {
            val testItem1 = com.example.foodwastetracker.data.database.entities.FoodItem(
                name = "Bananas",
                category = "Fruits",
                expirationDate = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000), // Expires in 2 days
                quantity = 5,
                unit = "pieces"
            )
            repository.addFoodItem(testItem1)

            val testItem2 = com.example.foodwastetracker.data.database.entities.FoodItem(
                name = "Milk",
                category = "Dairy",
                expirationDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // Expires in 7 days
                quantity = 1,
                unit = "bottle"
            )
            repository.addFoodItem(testItem2)
        }

        enableEdgeToEdge()
        setContent {
            FoodWasteTrackerTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF009688) // Beautiful green background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                navController = navController,
                                foodRepository = repository
                            )
                        }
                        composable("add_food") {
                            AddFoodScreen(
                                navController = navController,
                                foodRepository = repository
                            )
                        }
                        composable("statistics") {
                            StatisticsScreen(
                                navController = navController,
                                foodRepository = repository
                            )
                        }
                        composable("recipes") {
                            RecipesScreen(
                                navController = navController,
                                foodRepository = repository
                            )
                        }
                        composable("food_detail/{foodItemId}") { backStackEntry ->
                            val foodItemId = backStackEntry.arguments?.getString("foodItemId") ?: ""
                            FoodDetailScreen(
                                navController = navController,
                                foodRepository = repository,
                                foodItemId = foodItemId
                            )
                        }
                    }
                        }
                    }
                }
            }
        }
