package moe.caffeine.fridgehero.ui.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
  modifier: Modifier = Modifier,
  onDateSelected: (Long) -> Unit,
  onDismiss: () -> Unit,
  state: DatePickerState = rememberDatePickerState()
) {
  DatePickerDialog(
    modifier = modifier,
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = {
        state.selectedDateMillis?.let(onDateSelected)
        onDismiss()
      }) {
        Text("OK")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  ) {
    DatePicker(state = state)
  }
}
