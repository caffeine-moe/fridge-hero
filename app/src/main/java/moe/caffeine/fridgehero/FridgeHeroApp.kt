package moe.caffeine.fridgehero

import android.app.Application
import io.realm.kotlin.Realm
import moe.caffeine.fridgehero.repo.MongoRealm

class FridgeHeroApp : Application() {

    lateinit var realm : MongoRealm

    override fun onCreate() {
        super.onCreate()
        realm = MongoRealm
    }
}