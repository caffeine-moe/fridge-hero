package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                val profile = Profile().apply {
                    firstName = "John"
                    lastName = "Doe"
                }

                realm.updateObject(profile)

                val profiles = realm.fetchAllByType<Profile>()

                profiles.map { println(it.fullName) }
                println(profiles.size)

                profiles.map { realm.deleteObject(it) }
                println(realm.fetchAllByType<Profile>().size)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FridgeHeroTheme {
        Greeting("Android")
    }
}