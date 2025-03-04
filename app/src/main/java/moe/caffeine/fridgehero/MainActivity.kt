package moe.caffeine.fridgehero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
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
import moe.caffeine.fridgehero.ui.screen.InitialisationLoadingOverlay
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
                  MainScreen(
                    profile = profile,
                    foodItems = viewModel.foodItems,
                    eventFlow = viewModel.eventFlow,
                    emitEvent = emitEvent
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
          /*          runBlocking {
                      OpenFoodFactsTaxonomyParser.parse().getOrNull()?.forEach {
                        println(it.value.name)
                        println("----" + it.value.parents.keys)
                      }
                    }*/
          InitialisationLoadingOverlay(initialisationStage)
        }
      }
    }
  }
}
