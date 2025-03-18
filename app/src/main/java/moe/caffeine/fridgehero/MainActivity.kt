package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.MainViewModel
import moe.caffeine.fridgehero.ui.overlay.InitialisationLoadingOverlay
import moe.caffeine.fridgehero.ui.screen.main.MainScreen
import moe.caffeine.fridgehero.ui.screen.oobe.OOBE
import moe.caffeine.fridgehero.ui.theme.FridgeHeroTheme

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val profileState by viewModel.profile.collectAsStateWithLifecycle()
      val initialisationStage by viewModel.initialisationStage.collectAsStateWithLifecycle()
      val readyState by rememberSaveable(initialisationStage) {
        mutableStateOf(initialisationStage == InitialisationStage.Finished)
      }
      val emitEvent: (Event) -> Unit = { event -> viewModel.emitEvent(event) }
      FridgeHeroTheme {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          AnimatedVisibility(readyState) {
            profileState?.let { maybeProfile ->
              maybeProfile.fold(
                onSuccess = { profile ->
                  AnimatedVisibility(
                    visible = maybeProfile.isSuccess,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                  ) {
                    MainScreen(
                      profile = profile,
                      foodItems = viewModel.foodItems,
                      recipes = viewModel.recipes,
                      eventFlow = viewModel.eventFlow,
                      emitEvent = emitEvent
                    )
                  }
                },
                onFailure = {
                  AnimatedVisibility(
                    visible = maybeProfile.isFailure,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                  ) {
                    OOBE { firstName, lastName ->
                      viewModel.upsertProfile(
                        Profile(
                          firstName = firstName,
                          lastName = lastName
                        )
                      )
                    }
                  }
                }
              )
            }
          }
          InitialisationLoadingOverlay(initialisationStage)
        }
      }
    }
  }
}
