package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.MainViewModel
import moe.caffeine.fridgehero.ui.overlay.LoadingOverlay
import moe.caffeine.fridgehero.ui.screen.MainScreen
import moe.caffeine.fridgehero.ui.screen.oobe.OOBE
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val profileState by viewModel.profile.collectAsState()
      var showLoadingOverlay by rememberSaveable { mutableStateOf(true) }
      FridgeHeroTheme {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          LoadingOverlay(visible = showLoadingOverlay)
          profileState?.let { maybeProfile ->
            maybeProfile.fold(
              onSuccess = { profile ->
                showLoadingOverlay = false
                MainScreen(
                  profile = profile,
                  navBarItems = viewModel.navBarItems,
                  foodItems = viewModel.foodItems,
                  eventFlow = viewModel.eventFlow,
                  emitEvent = {
                    viewModel.emitEvent(it)
                  }
                )
              },
              onFailure = {
                OOBE { firstName, lastName ->
                  viewModel.upsertProfile(
                    Profile(
                      firstName = firstName,
                      lastName = lastName
                    )
                  )
                }
              }
            )
          }
        }
      }
    }
  }
}
