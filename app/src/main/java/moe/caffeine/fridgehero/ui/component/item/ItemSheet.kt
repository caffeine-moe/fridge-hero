package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheet(
  modifier: Modifier = Modifier,
  state: BottomSheetScaffoldState,
  onComplete: () -> Unit,
  onDismiss: () -> Unit,
  onResetRequest: () -> Unit,
  content: @Composable () -> Unit,
) {
  val scrollState = rememberScrollState()
  BottomSheetScaffold(
    scaffoldState = state,
    sheetPeekHeight = 260.dp,
    modifier = modifier
      .systemBarsPadding(),
    sheetContent = {
      Row(
        Modifier.padding(8.dp)
      ) {
        TextButton(onClick = onDismiss) {
          Text("Dismiss")
        }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onResetRequest) {
          Text("Reset")
        }
        Spacer(Modifier.weight(1f))
        TextButton(
          onClick = onComplete
        ) {
          Text("Save")
        }
      }
      Surface(modifier = Modifier.verticalScroll(scrollState)) {
        content()
      }
    }
  ) {}
}
