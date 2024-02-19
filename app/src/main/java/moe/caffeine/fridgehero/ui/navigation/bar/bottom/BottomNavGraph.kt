package moe.caffeine.fridgehero.ui.navigation.bar.bottom

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import moe.caffeine.fridgehero.screen.home.Home
import moe.caffeine.fridgehero.screen.myfridge.MyFridge
import moe.caffeine.fridgehero.screen.settings.Settings

@Composable
fun BottomNavGraph(
    tabBarItems : List<TabBarItem>,
    navController : NavHostController,
    barHeight : Dp,
    startDestination : String,
) {
    NavHost(
        navController = navController, startDestination,
    ) {
        composable(tabBarItems[0].title) {
            Home(barHeight)
        }
        composable(tabBarItems[1].title) {
            MyFridge(navController, barHeight)
        }
        composable(tabBarItems[2].title) {
            Settings(barHeight)
        }
    }
}