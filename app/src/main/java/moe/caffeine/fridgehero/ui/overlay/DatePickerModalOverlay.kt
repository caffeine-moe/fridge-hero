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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalOverlay(
  modifier: Modifier = Modifier,
  visible: Boolean = false,
  prefill: Long? = null,
  onComplete: (Result<Long>) -> Unit,
) {
  var calendar = Calendar.getInstance()
  val today = now().toLocalDateTime(TimeZone.currentSystemDefault())
  calendar.timeInMillis = prefill ?: now().toEpochMilliseconds()

  fun time() =
    calendar.toInstant().toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())

  val presetOptions = listOf(
    "1 Day" to 1,
    "3 Days" to 3,
    "1 Week" to 7,
    "1 Month" to 30,
    "3 Months" to 90,
    "1 Year" to 365
  )

  val dropdownData = listOf(
    "Day" to (1..time().month.maxLength()).map { it.toString().padStart(2, '0') },
    "Month" to (1..12).map { it.toString().padStart(2, '0') },
    "Year" to (((today.year))..(today.year + 50)).map { it.toString() }
  )

  if (!visible) return

  var displayDay by remember {
    mutableStateOf(
      calendar.get(Calendar.DAY_OF_MONTH).toString()
    )
  }
  var displayMonth by remember {
    mutableStateOf(
      (calendar.get(Calendar.MONTH) + 1).toString()
    )
  } // Calendar month is 0-based
  var displayYear by remember {
    mutableStateOf(calendar.get(Calendar.YEAR).toString())
  }

  fun updateDisplayDate() {
    displayDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
    displayMonth = (calendar.get(Calendar.MONTH) + 1).toString()
    displayYear = calendar.get(Calendar.YEAR).toString()
  }

  val expandedStates = dropdownData.map { remember { mutableStateOf(false) } }

  fun updateSelection(updatedDate: LocalDate) {
    calendar.apply {
      set(Calendar.YEAR, updatedDate.year)
      set(Calendar.DAY_OF_YEAR, updatedDate.dayOfYear)
    }
    updateDisplayDate()
  }

  fun updateDateFromLabel(label: String, value: String) {
    when (label) {
      "Day" -> calendar.apply { set(Calendar.DAY_OF_MONTH, value.toIntOrNull() ?: 1) }
      "Month" -> calendar.apply { set(Calendar.MONTH, value.toIntOrNull()?.minus(1) ?: 1) }
      "Year" -> calendar.apply { set(Calendar.YEAR, value.toIntOrNull() ?: 1970) }
      else -> return
    }
    updateDisplayDate()
  }

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
                      updateSelection(time().date.minus(days, DateTimeUnit.DAY))
                    },
                  ) {
                    Icon(
                      Icons.Filled.Remove,
                      "Remove ${days.days.inWholeDays} days off of date."
                    )
                  }
                  IconButton(
                    onClick = {
                      updateSelection(time().date.plus(days, DateTimeUnit.DAY))
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
                    value = when (label) {
                      "Day" -> displayDay.toString().padStart(2, '0')
                      "Month" -> displayMonth.toString().padStart(2, '0')
                      "Year" -> displayYear.toString()
                      else -> "0" // Should not happen
                    },
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
                          updateDateFromLabel(label, item)
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
                  time()
                    .date.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
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
