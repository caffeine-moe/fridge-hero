package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.InitialisationState
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
      val profileState by viewModel.profile.collectAsStateWithLifecycle()
      val initialisationState by viewModel.eventFlow.filter { it is Event.InitialisationStateBroadcast }
        .map { (it as Event.InitialisationStateBroadcast).state }
        .collectAsState(InitialisationState.INITIALISING)
      var readyState by rememberSaveable {
        mutableStateOf(false)
      }
      val emitEvent: (Event) -> Unit = { event -> viewModel.emitEvent(event) }
      LaunchedEffect(initialisationState) {
        if (initialisationState == InitialisationState.READY) {
          readyState = true
        }
      }
      FridgeHeroTheme {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          LoadingOverlay(
            visible = initialisationState != InitialisationState.READY,
            statusMessage = "Fridge Hero is Loading..."
          )
          profileState?.let { maybeProfile ->
            maybeProfile.fold(
              onSuccess = { profile ->
                viewModel.ensureReady()
                if (readyState) {
                  MainScreen(
                    profile = profile,
                    foodItems = viewModel.foodItems,
                    eventFlow = viewModel.eventFlow,
                    emitEvent = emitEvent
                  )
                }
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
