package com.grid.app.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.grid.app.ui.screens.AllTodosScreen
import com.grid.app.ui.screens.HomeScreen
import com.grid.app.ui.screens.TopicDetailScreen
import com.grid.app.ui.theme.GridBlack
import com.grid.app.ui.theme.GridCyan
import kotlinx.coroutines.launch

private const val NAV_ANIM_MS = 180

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridApp(viewModel: GridViewModel) {
    val navController = rememberNavController()
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = GridBlack,
        bottomBar = { GridBottomBar(navController = navController, pagerState = pagerState, scope = scope) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier
                .padding(padding)
                .background(GridBlack)
                .fillMaxSize(),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(NAV_ANIM_MS)) { it / 4 }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(NAV_ANIM_MS)) { -it / 4 }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(NAV_ANIM_MS)) { -it / 4 }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(NAV_ANIM_MS)) { it / 4 }
            }
        ) {
            composable("main") {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    if (page == 0) {
                        HomeScreen(viewModel = viewModel, onTopicClick = { id ->
                            navController.navigate("topic/$id")
                        })
                    } else {
                        AllTodosScreen(viewModel = viewModel)
                    }
                }
            }
            composable("topic/{topicId}") { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId")?.toLongOrNull() ?: 0L
                TopicDetailScreen(
                    topicId = topicId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            if (dragAmount > 12) {
                                change.consume()
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridBottomBar(
    navController: NavHostController,
    pagerState: PagerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val onMain = backStackEntry?.destination?.route == "main"
    val currentPage = pagerState.currentPage

    fun goToTab(index: Int) {
        if (!onMain) {
            navController.navigate("main") {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
        scope.launch { pagerState.animateScrollToPage(index) }
    }

    NavigationBar(containerColor = Color(0xFF0B0F14), contentColor = GridCyan) {
        NavigationBarItem(
            selected = onMain && currentPage == 0,
            onClick = { goToTab(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Topics") },
            label = { Text("Topics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GridCyan, selectedTextColor = GridCyan,
                indicatorColor = Color(0xFF10202A), unselectedIconColor = Color(0xFF5C7A8A), unselectedTextColor = Color(0xFF5C7A8A)
            )
        )
        NavigationBarItem(
            selected = onMain && currentPage == 1,
            onClick = { goToTab(1) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "All To-Dos") },
            label = { Text("All To-Dos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GridCyan, selectedTextColor = GridCyan,
                indicatorColor = Color(0xFF10202A), unselectedIconColor = Color(0xFF5C7A8A), unselectedTextColor = Color(0xFF5C7A8A)
            )
        )
    }
}
