package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.component.FloatingActionBar
import moe.caffeine.fridgehero.ui.component.ImageCard
import moe.caffeine.fridgehero.ui.component.TrailingEditIcon
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@Composable
fun RecipeEditorOverlay(
  visible: Boolean,
  prefill: Recipe,
  emitEvent: (Event) -> Unit,
  onComplete: (Result<Recipe>) -> Unit
) {
  val scrollState = rememberScrollState()
  val scope = rememberCoroutineScope()

  var editableRecipe by rememberSaveable(prefill) { mutableStateOf(prefill) }
  var interactedWithName by rememberSaveable { mutableStateOf(false) }

  AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }) + fadeIn(tween(500)),
    exit = slideOutHorizontally(tween(500), targetOffsetX = { 2 * it }) + fadeOut(tween(500))
  ) {
    Surface(
      Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      Column(
        Modifier
          .verticalScroll(scrollState)
          .padding(8.dp)
      ) {
        Spacer(Modifier.size(125.dp))
        ElevatedCard {
          Box(Modifier.padding(8.dp)) {
            Column {
              OutlinedTextField(
                isError = editableRecipe.name.isEmpty() && interactedWithName,
                label = { Text("Name") },
                modifier = Modifier
                  .fillMaxWidth(),
                value = editableRecipe.name,
                //readOnly = readOnly,
                onValueChange = {
                  interactedWithName = true
                  editableRecipe = editableRecipe.copy(name = it)
                },
                textStyle = MaterialTheme.typography.titleMedium,
                trailingIcon = { TrailingEditIcon() },
                placeholder = { Text("Name") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
              )
            }
          }
        }

        Spacer(Modifier.size(8.dp))

        ElevatedCard {
          Box(Modifier.padding(8.dp)) {
            Column {
              Row {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                  Text(
                    modifier = Modifier
                      .align(Alignment.CenterStart)
                      .padding(8.dp),
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium
                  )
                  TextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                      scope.launch {
                        Event.RequestItemsFromSearch {
                          onSuccess {
                            editableRecipe =
                              editableRecipe.copy(ingredients = editableRecipe.ingredients + it)
                          }
                        }.also(emitEvent)
                      }
                    }
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Add,
                      contentDescription = "Add Ingredient"
                    )
                  }
                }
              }
              LazyColumn(Modifier.height(260.dp)) {
                items(editableRecipe.ingredients.toList(), key = { it.realmId }) { foodItem ->
                  var expanded by rememberSaveable { mutableStateOf(false) }
                  var potentialMatches: List<FoodItem> by rememberSaveable {
                    mutableStateOf(
                      listOf()
                    )
                  }
                  Box(Modifier.padding(4.dp)) {
                    ActionableSwipeToDismissBox(
                      startToEndColor = Color.Transparent,
                      startToEndIcon = null,
                      onEndToStartAction = {
                        potentialMatches = listOf()
                        expanded = false
                        editableRecipe =
                          editableRecipe.copy(ingredients = editableRecipe.ingredients - foodItem)
                      }
                    ) {
                      Box(Modifier.fillMaxSize()) {

                        ItemCard(
                          elevation = CardDefaults.elevatedCardElevation(4.dp),
                          item = foodItem, expanded = expanded,
                          extraContent = {
                            if (foodItem.isRemoved) {
                              LaunchedEffect(Unit) {
                                Event.FindPotentialMatches(foodItem) {
                                  onSuccess {
                                    potentialMatches = it
                                  }
                                }
                              }
                              Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End) {
                                TextButton(
                                  onClick = {
                                    if (potentialMatches.isNotEmpty()) {
                                      expanded = !expanded
                                    }
                                  },
                                ) {
                                  Icon(Icons.Filled.Warning, null)
                                  Spacer(Modifier.size(4.dp))
                                  Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    softWrap = true,
                                    style = MaterialTheme.typography.labelMedium,
                                    text = "Item not found in fridge" +
                                            (if (potentialMatches.isNotEmpty())
                                              ", click to view potential replacements"
                                            else "") + "."
                                  )
                                }
                              }
                            }
                          }) {
                          potentialMatches.forEach {
                            Column {
                              Box(Modifier.padding(4.dp)) {
                                ItemCard(item = it)
                              }
                            }
                          }
                        }

                        Box(
                          Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxSize()
                        ) {
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(Modifier.size(8.dp))

        ElevatedCard {
          Box(Modifier.padding(8.dp)) {
            Column {
              Text(
                modifier = Modifier
                  .padding(8.dp),
                text = "Instructions",
                style = MaterialTheme.typography.titleMedium
              )
              OutlinedTextField(
                modifier = Modifier
                  .padding(8.dp)
                  .defaultMinSize(minHeight = 80.dp)
                  .fillMaxWidth(),
                value = editableRecipe.instructions,
                onValueChange = {
                  editableRecipe = editableRecipe.copy(instructions = it)
                }
              )
            }
          }
        }

        Spacer(Modifier.size(68.dp))
      }

      Column {
        Box(
          Modifier
            .wrapContentSize()
            .fillMaxWidth()
            .height(125.dp)
        ) {
          ImageCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                scope.launch {
                  Event.RequestExternalImage {
                    onSuccess {
                      editableRecipe = editableRecipe.copy(imageByteArray = it)
                    }
                  }
                }
              },
            editableRecipe.imageByteArray,
            ContentScale.Crop
          )
          Text(
            text = editableRecipe.name,
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(8.dp)
          )
        }
      }

      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        FloatingActionBar(
          true,
          listOf(
            ("Save" to {
              onComplete(Result.success(editableRecipe))
            }),
            ("Reset" to {
              editableRecipe = prefill
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
