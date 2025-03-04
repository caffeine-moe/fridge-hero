package moe.caffeine.fridgehero.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.initialisation.InitialisationState
import moe.caffeine.fridgehero.ui.overlay.LoadingOverlay

@Composable
fun InitialisationLoadingOverlay(initialisationStage: InitialisationStage) {
  val progress by initialisationStage.progress.collectAsStateWithLifecycle()
  LoadingOverlay(
    initialisationStage.state != InitialisationState.READY,
    statusMessage = "Fridge Hero is getting things ready for you...",
    definitiveProgress = progress,
    extraContent = {
      Row {
        Text(
          initialisationStage.statusMessage
        )
        if (initialisationStage is InitialisationStage.TaxonomyInitialisation) {
          Image(
            painter = painterResource(id = R.drawable.off_logo),
            contentDescription = "Progress Icon",
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  )
}
