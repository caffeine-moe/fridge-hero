package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.home.Home
import moe.caffeine.fridgehero.model.Profile
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
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Scaffold(
                        Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                    ) {
                        Home(Profile().apply { firstName = "James"; lastName = "Doe" })
                    }
                }
            }
        }
    }
}