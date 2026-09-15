package com.grid.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.grid.app.ui.screens.AllTodosScreen
import com.grid.app.ui.screens.HomeScreen
import com.grid.app.ui.screens.TopicDetailScreen
import com.grid.app.ui.theme.GridBlack
import com.grid.app.ui.theme.GridCyan

@Composable
fun GridApp(viewModel: GridViewModel) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = GridBlack,
        bottomBar = { GridBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(padding)
                .background(GridBlack)
                .fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(viewModel = viewModel, onTopicClick = { id ->
                    navController.navigate("topic/$id")
                })
            }
            composable("all_todos") {
                AllTodosScreen(viewModel = viewModel)
            }
            composable("topic/{topicId}") { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId")?.toLongOrNull() ?: 0L
                TopicDetailScreen(
                    topicId = topicId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun GridBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun singleTop(): NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.startDestinationId) { saveState = true }
    }

    NavigationBar(containerColor = Color(0xFF0B0F14), contentColor = GridCyan) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home", singleTop()) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Topics") },
            label = { Text("Topics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GridCyan, selectedTextColor = GridCyan,
                indicatorColor = Color(0xFF10202A), unselectedIconColor = Color(0xFF5C7A8A), unselectedTextColor = Color(0xFF5C7A8A)
            )
        )
        NavigationBarItem(
            selected = currentRoute == "all_todos",
            onClick = { navController.navigate("all_todos", singleTop()) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "All To-Dos") },
            label = { Text("All To-Dos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GridCyan, selectedTextColor = GridCyan,
                indicatorColor = Color(0xFF10202A), unselectedIconColor = Color(0xFF5C7A8A), unselectedTextColor = Color(0xFF5C7A8A)
            )
        )
    }
}
