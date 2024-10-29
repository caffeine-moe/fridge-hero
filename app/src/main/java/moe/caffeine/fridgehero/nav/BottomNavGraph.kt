package moe.caffeine.fridgehero.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navItems: List<BottomNavItem>
) {
    NavHost(
        navController = navController,
        "Home"
    ) {
        navItems.forEach { item ->
            composable(item.title) {
                item.destination()
            }
        }
    }
}