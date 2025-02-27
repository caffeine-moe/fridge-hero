package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.ui.component.DatePickerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerOverlay(
  visible: Boolean,
  state: DatePickerState,
  onComplete: (Result<Long>) -> Unit,
) {
  AnimatedVisibility(visible) {
    DatePickerModal(
      state = state,
      onDateSelected = { date ->
        onComplete(Result.success(date))
      },
      onDismiss = {
        onComplete(Result.failure(Throwable("Dismissed")))
      }
    )
  }
}
