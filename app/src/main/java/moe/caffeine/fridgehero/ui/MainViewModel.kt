package moe.caffeine.fridgehero.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.copyFromRealm
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.realm.FoodItem
import moe.caffeine.fridgehero.data.realm.MongoRealm
import moe.caffeine.fridgehero.data.realm.Profile
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.fridge.Fridge
import moe.caffeine.fridgehero.ui.home.Home
import moe.caffeine.fridgehero.ui.nav.BottomNavItem
import moe.caffeine.fridgehero.ui.recipe.Recipes

class MainViewModel : ViewModel() {
  private val realm = MongoRealm
  private val openFoodFactsApi = OpenFoodFactsApi

  //misc

  suspend fun createProfile(firstName: String, lastName: String): CompletableDeferred<Profile> =
    withContext(Dispatchers.Default) {
      val profile = Profile().apply {
        this.firstName = firstName
        this.lastName = lastName
      }
      realm.updateObject(profile)
      CompletableDeferred(profile)
    }

  //data flow stuff

  val profiles = realm
    .fetchAllByTypeAsFlow<Profile>()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(),
      emptyList()
    )

  private val foodItems = realm
    .fetchAllByTypeAsFlow<FoodItem>()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(),
      emptyList()
    )

  private val fridgeItems = foodItems
    .map { managedList ->
      withContext(Dispatchers.Default) {
        realm.realm.copyFromRealm(managedList)
      }
    }
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(),
      emptyList()
    )

  // UI stuff

  val navBarItems =
    listOf(
      BottomNavItem(
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home
      ) { Home(realm.fetchAllByType<Profile>().first()) },
      BottomNavItem(
        "Fridge",
        Icons.Filled.Kitchen,
        Icons.Outlined.Kitchen
      ) {
        Fridge(
          fridgeItems,
          emitEvent = { event ->
            emitEvent(event)
          }
        )
      },
      BottomNavItem(
        "Recipes",
        Icons.Filled.Dining,
        Icons.Outlined.Dining,
      ) { Recipes() }
    )

  //event stuff

  private val _eventFlow = MutableSharedFlow<Event>()
  val eventFlow: SharedFlow<Event> = _eventFlow

  fun emitEvent(event: Event) = viewModelScope.launch { _eventFlow.emit(event) }

  init {
    viewModelScope.launch(Dispatchers.Default) {
      eventFlow.collectLatest { event ->
        when (event) {
          is Event.RequestFoodItemFromBarcode -> {
            event.result.complete(processBarcode(event.barcode).await())
          }

          is Event.RequestLiveFoodItemOperation -> {
            val fetchManagedResult =
              realm.fetchObjectById<FoodItem>(event.foodItem._id)
            fetchManagedResult.fold(
              onSuccess = { managed ->
                realm.tryWriteToLatest(managed, event.operation)
              },
              onFailure = {
                return@collectLatest
              }
            )
          }

          is Event.UpsertFoodItem -> {
            event.result.complete(
              realm.updateObject(event.foodItem)
            )
          }

          is Event.RemoveFromDatabase -> {
            realm.deleteObject(event.realmObject)
          }

          else -> return@collectLatest
        }
      }
    }
  }

  //barcode stuff

  suspend fun fetchFoodItemFromBarcode(
    barcode: String,
  ): CompletableDeferred<Result<FoodItem>> {
    val fetchResult = openFoodFactsApi.fetchProductByBarcode(barcode).await()
    fetchResult.fold(
      onFailure = { result ->
        return CompletableDeferred(Result.failure(result))
      },
      onSuccess = { result ->
        return CompletableDeferred(Result.success(result.asFoodItem()))
      }
    )
  }

  private suspend fun processBarcode(barcode: String): CompletableDeferred<Result<FoodItem>> =
    withContext(Dispatchers.IO) {
      emitEvent(Event.DisplayToast("Processing barcode $barcode, please wait..."))
      CompletableDeferred(
        foodItems.value.firstOrNull { it.barcode == barcode }?.let {
          Result.success(it.copyFromRealm())
        } ?: fetchFoodItemFromBarcode(
          barcode
        ).await().fold(
          onSuccess = { success ->
            emitEvent(Event.DisplayToast("Successfully retrieved ${success.name}"))
            Result.success(success)
          },
          onFailure = { failure ->
            emitEvent(Event.DisplayToast("ERROR: ${failure.message}"))
            Result.failure(failure)
          }
        )
      )
    }
}
