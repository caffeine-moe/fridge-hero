package moe.caffeine.fridgehero.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
  val route: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val hasFloatingActionButton: Boolean = false,
) {
  val title: String
    get() = route.replaceFirstChar { it.titlecaseChar() }

  data object Home : Screen(
    "home",
    Icons.Filled.Home,
    Icons.Outlined.Home
  )

  data object Fridge : Screen(
    "fridge",
    Icons.Filled.Kitchen,
    Icons.Outlined.Kitchen,
    true
  )

  data object Recipes : Screen(
    "recipes",
    Icons.Filled.Dining,
    Icons.Outlined.Dining,
    true
  )
}
