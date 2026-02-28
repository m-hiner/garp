package com.gardencompanion.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gardencompanion.presentation.screen.GardenPlansScreen
import com.gardencompanion.presentation.screen.PlanDetailScreen
import com.gardencompanion.presentation.screen.PlantSelectionScreen
import com.gardencompanion.presentation.screen.SettingsScreen
import com.gardencompanion.presentation.screen.SubPlotDetailScreen

@Composable
fun GardenNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.Plans,
    ) {
        composable(NavigationDestinations.Plans) {
            GardenPlansScreen(
                onOpenPlan = { planId, year -> navController.navigate(NavigationDestinations.planDetail(planId, year)) },
                onOpenSettings = { navController.navigate(NavigationDestinations.Settings) },
            )
        }

        composable(
            route = NavigationDestinations.PlanDetail,
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("year") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: return@composable
            val year = backStackEntry.arguments?.getInt("year") ?: return@composable
            PlanDetailScreen(
                planId = planId,
                year = year,
                onBack = { navController.popBackStack() },
                onOpenSubPlot = { subPlotId -> navController.navigate(NavigationDestinations.subPlotDetail(planId, year, subPlotId)) },
            )
        }

        composable(
            route = NavigationDestinations.SubPlotDetail,
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("year") { type = NavType.IntType },
                navArgument("subPlotId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: return@composable
            val year = backStackEntry.arguments?.getInt("year") ?: return@composable
            val subPlotId = backStackEntry.arguments?.getString("subPlotId") ?: return@composable
            SubPlotDetailScreen(
                planId = planId,
                year = year,
                subPlotId = subPlotId,
                onBack = { navController.popBackStack() },
                onChoosePlant = { rowId -> navController.navigate(NavigationDestinations.plantSelection(planId, year, subPlotId, rowId)) },
            )
        }

        composable(
            route = NavigationDestinations.PlantSelection,
            arguments = listOf(
                navArgument("planId") { type = NavType.StringType },
                navArgument("year") { type = NavType.IntType },
                navArgument("subPlotId") { type = NavType.StringType },
                navArgument("rowId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: return@composable
            val year = backStackEntry.arguments?.getInt("year") ?: return@composable
            val subPlotId = backStackEntry.arguments?.getString("subPlotId") ?: return@composable
            val rowId = backStackEntry.arguments?.getString("rowId") ?: return@composable
            PlantSelectionScreen(
                planId = planId,
                year = year,
                subPlotId = subPlotId,
                rowId = rowId,
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() },
            )
        }

        composable(NavigationDestinations.Settings) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
