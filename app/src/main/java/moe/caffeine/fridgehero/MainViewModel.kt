package moe.caffeine.fridgehero

import androidx.lifecycle.ViewModel
import moe.caffeine.fridgehero.repo.MongoRealm

class MainViewModel : ViewModel() {
    val realm = MongoRealm
}