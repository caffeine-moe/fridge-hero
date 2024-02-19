package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.screen.setup.Setup
import moe.caffeine.fridgehero.ui.navigation.bar.MainScreen
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme
import moe.caffeine.fridgehero.user.config.ProfileConfig
import moe.caffeine.fridgehero.user.fetchProfiles
import moe.caffeine.fridgehero.user.profile.ProfileImpl
import moe.caffeine.fridgehero.util.profile

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridgeHeroTheme {
                Box(Modifier.navigationBarsPadding()) {
                    val profiles = fetchProfiles()
                    if (profiles.isEmpty()) {
                        Setup()
                    } else {
                        profile = ProfileImpl("0", ProfileConfig("John", "Pork"))
                        MainScreen()
                    }
                }

            }
        }
    }
}