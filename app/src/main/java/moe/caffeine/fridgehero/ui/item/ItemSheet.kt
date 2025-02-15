package moe.caffeine.fridgehero.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.data.realm.FoodItem
import moe.caffeine.fridgehero.ui.item.components.ExpiryEditor
import moe.caffeine.fridgehero.ui.item.components.ItemEditor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheet(
  editableFoodItem: FoodItem,
  editableExpiryDates: List<Long>,
  modifier: Modifier = Modifier,
  onDismiss: () -> Unit,
  onComplete: (Pair<FoodItem, List<Long>>) -> Unit,
  onResetRequest: () -> Unit,
  onScannerRequest: (replaceAll: Boolean) -> Unit,
  onExpiryAddRequest: () -> Unit,
  onExpiryDuplicateRequest: (Long) -> Unit,
  onExpiryRemoveRequest: (Long) -> Unit,
  onEditorFieldChanged: (FoodItem) -> Unit,
) {
  val scrollState = rememberScrollState()
  ModalBottomSheet(
    modifier = modifier
      .systemBarsPadding(),
    onDismissRequest = onDismiss
  ) {
    Row(
      Modifier.padding(8.dp)
    ) {
      TextButton(onClick = {
        onComplete(Pair(editableFoodItem, editableExpiryDates))
      }) {
        Text("Save")
      }
      Spacer(Modifier.weight(1f))
      TextButton(onClick = onResetRequest) {
        Text("Reset")
      }
    }
    Surface(
      Modifier
        .verticalScroll(scrollState)
    ) {
      Column {
        ItemEditor(
          editableFoodItem,
          onScannerRequest = onScannerRequest,
          onFieldChanged = onEditorFieldChanged,
        )
        ExpiryEditor(
          editableExpiryDates,
          onExpiryAddRequest = onExpiryAddRequest,
          onExpiryDuplicateRequest = onExpiryDuplicateRequest,
          onExpiryRemoveRequest = onExpiryRemoveRequest,
        )
      }
    }
  }
}
