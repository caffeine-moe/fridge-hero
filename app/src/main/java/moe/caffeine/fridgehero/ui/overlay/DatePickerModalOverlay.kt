package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalOverlay(
  modifier: Modifier = Modifier,
  visible: Boolean = false,
  onComplete: (Result<Long>) -> Unit,
) {
  val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

  val presetOptions = listOf(
    "1 Day" to 1,
    "3 Days" to 3,
    "1 Week" to 7,
    "1 Month" to 30,
    "3 Months" to 90,
    "1 Year" to 365
  )

  val dropdownData = listOf(
    "Day" to (1..31).map { it.toString().padStart(2, '0') },
    "Month" to (1..12).map { it.toString().padStart(2, '0') },
    "Year" to ((now.year)..(now.year + 50)).map { it.toString() }
  )

  if (!visible) return

  val selectedValues = dropdownData.map {
    remember {
      mutableStateOf(
        when (it.first) {
          "Day" -> now.dayOfMonth.toString().padStart(2, '0')
          "Month" -> now.monthNumber.toString().padStart(2, '0')
          "Year" -> now.year.toString()
          else -> it.second[0]
        }
      )
    }
  }

  val selectedDate by remember(selectedValues) {
    derivedStateOf {
      LocalDate(
        selectedValues[2].value.toInt(),
        selectedValues[1].value.toInt(),
        selectedValues[0].value.toInt()
      )
    }
  }

  val expandedStates = dropdownData.map { remember { mutableStateOf(false) } }

  Dialog(
    onDismissRequest = { onComplete(Result.failure(Throwable("Dismissed"))) },
    properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)
  ) {
    Surface(
      Modifier
        .fillMaxWidth(0.9f),
      shape = MaterialTheme.shapes.medium,
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "Date",
          style = MaterialTheme.typography.titleMedium
        )

        Column {

          fun updateSelection(updatedDate: LocalDate) {
            selectedValues[0].value = updatedDate.dayOfMonth.toString().padStart(2, '0')
            selectedValues[1].value = updatedDate.monthNumber.toString().padStart(2, '0')
            selectedValues[2].value = updatedDate.year.toString()
          }

          @Composable
          fun Preset(days: Int, label: String) {
            Row(
              modifier = Modifier
                .padding(2.dp)
                .weight(1f),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Row {
                  IconButton(
                    onClick = {
                      updateSelection(selectedDate.minus(days, DateTimeUnit.DAY))
                    },

                    ) {
                    Icon(
                      Icons.Filled.Remove,
                      "Remove ${days.days.inWholeDays} days off of date."
                    )
                  }
                  IconButton(
                    onClick = {
                      updateSelection(selectedDate.plus(days, DateTimeUnit.DAY))
                    },
                  ) {
                    Icon(
                      Icons.Filled.Add,
                      "Add ${days.days.inWholeDays} days to date."
                    )
                  }
                }
              }
            }
          }

          ElevatedCard {
            Row(Modifier.fillMaxWidth()) {
              presetOptions.take(3).forEach { (label, days) ->
                Preset(days, label)
              }
            }
          }

          Spacer(Modifier.size(8.dp))

          ElevatedCard {
            Row(Modifier.fillMaxWidth()) {
              presetOptions.takeLast(3).forEach { (label, days) ->
                Preset(days, label)
              }
            }
          }

          Spacer(Modifier.size(8.dp))


          Row {
            dropdownData.forEachIndexed { index, (label, items) ->
              ExposedDropdownMenuBox(
                modifier = Modifier
                  .weight(1f)
                  .wrapContentSize(),
                expanded = expandedStates[index].value,
                onExpandedChange = {
                  expandedStates[index].value = !expandedStates[index].value
                }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  OutlinedTextField(
                    value = selectedValues[index].value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(label) },
                    singleLine = true,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    trailingIcon = {
                      ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedStates[index].value
                      )
                    }
                  )
                  ExposedDropdownMenu(
                    expanded = expandedStates[index].value,
                    onDismissRequest = {
                      expandedStates[index].value = false
                    }
                  ) {
                    items.forEach { item ->
                      DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                          selectedValues[index].value = item
                          expandedStates[index].value = false
                        }
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(Modifier.size(8.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { onComplete(Result.failure(Throwable("Dismissed"))) },
              modifier = Modifier.weight(1f)
            ) {
              Text("Dismiss")
            }
            Button(
              onClick = {
                onComplete(Result.success(-1L))
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Never")
            }
            Button(
              onClick = {
                val timestamp = runCatching {
                  selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                }.getOrDefault(-1L)
                onComplete(Result.success(timestamp))
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Select")
            }
          }
        }
      }
    }
  }
}
