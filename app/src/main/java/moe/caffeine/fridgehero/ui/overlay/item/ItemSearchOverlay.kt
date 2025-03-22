package moe.caffeine.fridgehero.ui.overlay.item

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.helper.fuzzyMatch
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.CustomSearchBar
import moe.caffeine.fridgehero.ui.component.FloatingActionBar
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@OptIn(
  ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
  ExperimentalLayoutApi::class
)
@Composable
fun ItemSearchOverlay(
  visible: Boolean,
  foodItems: StateFlow<List<FoodItem>>,
  onComplete: (Result<List<FoodItem>>) -> Unit
) {
  val searchable by foodItems.collectAsStateWithLifecycle()
  var selectedItems by remember { mutableStateOf(setOf<FoodItem>()) }
  AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }) + fadeIn(tween(500)),
    exit = slideOutHorizontally(tween(500), targetOffsetX = { 2 * it }) + fadeOut(tween(500))
  ) {
    BackHandler {
      selectedItems = setOf()
      onComplete(Result.failure(Throwable("Dismissed")))
    }
    var query by rememberSaveable { mutableStateOf("") }
    Surface(
      Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      Column(Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box {
            Text(
              textAlign = TextAlign.Center,
              text = "Item Search",
              style = MaterialTheme.typography.headlineSmall,
            )
          }
        }
        LazyColumn {
          stickyHeader {
            Column(
              Modifier
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface)
            ) {
              CustomSearchBar(
                query = query,
                onTextChanged = {
                  query = it
                }
              )
              Spacer(Modifier.size(8.dp))
              androidx.compose.animation.AnimatedVisibility(
                visible = selectedItems.isNotEmpty(),
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(250))
              ) {
                ElevatedCard(
                  Modifier
                    .fillMaxWidth()
                ) {
                  if (selectedItems.isNotEmpty()) {
                    Text(
                      style = MaterialTheme.typography.labelLarge,
                      text = "Selected Items",
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                  FlowRow {
                    selectedItems.forEach {
                      androidx.compose.animation.AnimatedVisibility(
                        visible = selectedItems.contains(it),
                        enter = expandIn(tween(500), expandFrom = Alignment.Center),
                        exit = shrinkOut(tween(500), shrinkTowards = Alignment.Center)
                      ) {
                        TextButton(
                          modifier = Modifier.padding(vertical = 0.dp),
                          onClick = {
                            selectedItems = selectedItems - it
                          }) {
                          Text(it.name)
                        }
                      }
                    }
                  }
                }
              }
              Spacer(Modifier.size(8.dp))
            }
          }
          items(searchable) {
            val matches by remember {
              derivedStateOf {
                when {
                  query.isEmpty() -> {
                    true
                  }

                  else -> fuzzyMatch(it.name, query)
                }
              }
            }
            AnimatedVisibility(matches) {
              ElevatedCard(
                Modifier
                  .padding(4.dp)
              ) {
                ItemCard(
                  modifier = Modifier.clickable {
                    selectedItems = selectedItems + it
                  },
                  item = it
                )
              }
            }
          }
        }
      }
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        FloatingActionBar(
          true,
          listOf(
            ("Confirm" to {
              onComplete(Result.success(selectedItems.toList()))
            }),
            ("Reset" to {
              selectedItems = setOf()
            }),
            ("Dismiss" to {
              onComplete(Result.failure(Throwable("Dismissed")))
            })
          )
        )
      }
    }
  }
}
