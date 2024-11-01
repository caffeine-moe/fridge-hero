package moe.caffeine.fridgehero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.model.FoodItem
import moe.caffeine.fridgehero.model.Profile
import moe.caffeine.fridgehero.repo.MongoRealm

class MainViewModel : ViewModel() {
    val realm = MongoRealm

    val foodItems = realm
        .fetchAllByTypeAsFlow<FoodItem>()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun addToRealm(realmObject: RealmObject) {
        viewModelScope.launch {
            realm.updateObject(realmObject)
        }
    }

    fun removeFromRealm(realmObject: RealmObject) {
        viewModelScope.launch {
            realm.deleteObject(
                realmObject
            )
        }
    }

    fun createProfile(firstName: String, lastName: String): Profile {
        val profile = Profile().apply {
            this.firstName = firstName
            this.lastName = lastName
        }
        realm.updateObject(profile)
        return profile
    }
}