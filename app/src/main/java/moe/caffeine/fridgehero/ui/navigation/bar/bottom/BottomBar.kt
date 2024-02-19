package moe.caffeine.fridgehero.ui.navigation.bar.bottom

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(tabBarItems : List<TabBarItem>, navController : NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    BottomAppBar(
        modifier = Modifier
            .navigationBarsPadding()
    ) {
        tabBarItems.forEachIndexed { index, tabBarItem ->

            NavigationBarItem(
                modifier = Modifier.fillMaxSize(),
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.popBackStack()
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (tabBarItem.badgeAmount != null) {
                                Badge {
                                    Text(tabBarItem.badgeAmount.toString())
                                }
                            }
                        }) {
                        Icon(
                            imageVector = if (selectedTabIndex == index) {
                                tabBarItem.selectedIcon
                            } else {
                                tabBarItem.unselectedIcon
                            },
                            contentDescription = tabBarItem.title
                        )
                    }
                },
                label = {
                    AnimatedVisibility(
                        visible = selectedTabIndex == index,
                        enter =
                        slideInVertically(
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = LinearEasing // interpolator
                            )
                        ) +
                                fadeIn(
                                    animationSpec = tween(
                                        400,
                                        easing = LinearEasing
                                    )
                                ),
                        exit = slideOutVertically(
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = LinearEasing
                            )
                        ) + fadeOut(
                            animationSpec = tween(
                                400, easing = LinearEasing
                            )
                        )
                    )
                    {
                        Text(tabBarItem.title)
                    }
                }
            )
        }
    }
}