package moe.caffeine.fridgehero

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.model.FoodItem
import moe.caffeine.fridgehero.model.Profile
import moe.caffeine.fridgehero.openfoodfacts.OpenFoodFactsApi
import moe.caffeine.fridgehero.repo.MongoRealm

class MainViewModel : ViewModel() {
    val realm = MongoRealm
    val openFoodFactsApi = OpenFoodFactsApi

    var destination by mutableStateOf("Home")

    val foodItems = realm
        .fetchAllByTypeAsFlow<FoodItem>()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val fridgeItems =
        foodItems.map { foodItems ->
            foodItems.filter { !it.isRemoved }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    fun addToRealm(realmObject: RealmObject) = viewModelScope.launch {
        realm.updateObject(realmObject)
    }

    fun removeFromRealm(realmObject: RealmObject) = viewModelScope.launch {
        realm.deleteObject(realmObject)
    }

    fun updateRemovedState(foodItem: FoodItem, isRemoved: Boolean = true) = viewModelScope.launch {
        realm.updateObject(foodItem.copyFromRealm().apply { this.isRemoved = isRemoved })
    }

    fun addExisting(foodItem: FoodItem, expiry: Long) = viewModelScope.launch {
        if (foodItem.isRemoved) updateRemovedState(foodItem, false)
        realm.updateObject(foodItem.copyFromRealm().apply { expiryDates += expiry })
    }

    fun createProfile(firstName: String, lastName: String): Profile {
        val profile = Profile().apply {
            this.firstName = firstName
            this.lastName = lastName
        }
        addToRealm(profile)
        return profile
    }

    fun createFoodItemFromBarcode(
        barcode: String,
    ) = viewModelScope.launch {
        val fetchResult = openFoodFactsApi.fetchProductByBarcode(barcode)
        fetchResult.fold(
            onFailure = { result ->
                _toastMessage.emit(
                    "ERROR: ${result.message}"
                )
            },
            onSuccess = { result ->
                val foodItem = result.asFoodItem()
                _toastMessage.emit("Successfully scanned ${foodItem.name}!")
                addToRealm(foodItem)
            }
        )
    }

    fun addFoodItemFromBarcode(barcode: String) = viewModelScope.launch {
        foodItems.value.firstOrNull { it.barcode == barcode }.also { foodItem ->
            if (foodItem == null) {
                createFoodItemFromBarcode(barcode)
                return@launch
            }
            addExisting(foodItem, 0)
        }
    }
}