package moe.caffeine.fridgehero.ui.navigation.bar.bottom

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import moe.caffeine.fridgehero.home.Home
import moe.caffeine.fridgehero.myfridge.MyFridge
import moe.caffeine.fridgehero.settings.Settings

@Composable
fun BottomNavGraph(tabBarItems : List<TabBarItem>, navController : NavHostController, insets : WindowInsets) {

    NavHost(navController = navController, startDestination = tabBarItems[0].title) {
        composable(tabBarItems[0].title) {
            Home(insets)
        }
        composable(tabBarItems[1].title) {
            MyFridge()
        }
        composable(tabBarItems[2].title) {
            Settings()
        }
    }
}