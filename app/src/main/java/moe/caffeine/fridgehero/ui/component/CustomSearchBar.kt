package moe.caffeine.fridgehero.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
  query: String,
  modifier: Modifier = Modifier,
  onFocusState: (FocusState) -> Unit = {},
  onTextChanged: (String) -> Unit,
) {
  var showClearIcon by rememberSaveable { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current
  SearchBar(
    modifier = modifier
      .onFocusChanged {
        showClearIcon = it.isFocused
        onFocusState(it)
      },
    shape = SearchBarDefaults.inputFieldShape,
    expanded = false,
    inputField = {
      SearchBarDefaults.InputField(
        expanded = false,
        query = query,
        onQueryChange = onTextChanged,
        onSearch = {},
        onExpandedChange = {
          showClearIcon = it
        },
        trailingIcon = {
          if (!showClearIcon) return@InputField
          IconButton(
            onClick = {
              if (query == "")
                focusManager.clearFocus()
              else
                onTextChanged("")
            }
          ) {
            Icon(
              Icons.Filled.Clear,
              "Clear search"
            )
          }
        },
        leadingIcon = {
          Icon(
            Icons.Filled.Search,
            null
          )
        }
      )
    },
    onExpandedChange = {}
  ) {}
}
