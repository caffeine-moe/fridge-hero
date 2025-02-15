package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.ui.MainScreen
import moe.caffeine.fridgehero.ui.MainViewModel
import moe.caffeine.fridgehero.ui.oobe.OOBE
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val scope = rememberCoroutineScope()
      val profiles = viewModel.profiles.collectAsState()
      FridgeHeroTheme {
        if (profiles.value.isEmpty()) {
          OOBE { firstName, lastName ->
            scope.launch {
              viewModel.createProfile(firstName, lastName)
            }
          }
        }
        MainScreen(
          viewModel.navBarItems,
          eventFlow = viewModel.eventFlow,
          emitEvent = {
            viewModel.emitEvent(it)
          }
        )
      }
    }
  }
}
