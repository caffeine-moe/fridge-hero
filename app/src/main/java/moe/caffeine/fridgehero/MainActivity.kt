package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import moe.caffeine.fridgehero.home.Home
import moe.caffeine.fridgehero.model.Profile
import moe.caffeine.fridgehero.nav.BottomNavBar
import moe.caffeine.fridgehero.nav.BottomNavGraph
import moe.caffeine.fridgehero.nav.BottomNavItem
import moe.caffeine.fridgehero.oobe.OOBE
import moe.caffeine.fridgehero.repo.MongoRealm
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

    private val realm = MongoRealm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeHeroTheme {
/*                realm.fetchAllByType<Profile>().ifEmpty {
                    OOBE(this)
                }*/
                val navController = rememberNavController()
                val profile = Profile().apply { firstName = "James"; lastName = "Doe" }
                val navBarItems =
                    listOf(
                        BottomNavItem(
                            "Home",
                            Icons.Filled.Home,
                            Icons.Outlined.Home,
                            destination = { Home(profile) }),
                        BottomNavItem(
                            "OOBE",
                            Icons.Filled.AccountBox,
                            Icons.Outlined.AccountBox,
                            destination = { OOBE() })
                    )
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        bottomBar = {
                            BottomNavBar(navController, navBarItems)
                        }
                    ) { paddingValues ->
                        Column(
                            Modifier.padding(paddingValues)
                        ) {
                            BottomNavGraph(navController, navBarItems)
                        }
                    }
                }
            }
        }
    }
}