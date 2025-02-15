package moe.caffeine.fridgehero.ui.component.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.component.DatePickerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerOverlay(
  datePickerRequest: Event.RequestDateFromPicker?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(datePickerRequest != null) {
    datePickerRequest?.result?.let { completable ->
      DatePickerModal(
        onDateSelected = { date ->
          completable.complete(Result.success(date))
        },
        onDismiss = {
          onDismiss()
          completable.complete(Result.failure(Throwable("Dismissed")))
        }
      )
    }
  }
}
