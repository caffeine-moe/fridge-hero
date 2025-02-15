package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.MainScreen
import moe.caffeine.fridgehero.ui.MainViewModel
import moe.caffeine.fridgehero.ui.oobe.OOBE
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FridgeHeroTheme {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          viewModel.profile?.let { profile ->
            MainScreen(
              profile = profile,
              navBarItems = viewModel.navBarItems,
              foodItems = viewModel.foodItems,
              eventFlow = viewModel.eventFlow,
              emitEvent = {
                viewModel.emitEvent(it)
              }
            )
            return@Surface
          }
          OOBE { firstName, lastName ->
            viewModel.repository.upsertProfile(
              Profile(
                firstName = firstName,
                lastName = lastName
              )
            )
          }
        }
      }
    }
  }
}
