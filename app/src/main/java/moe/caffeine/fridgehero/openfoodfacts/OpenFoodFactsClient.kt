package moe.caffeine.fridgehero.openfoodfacts

import io.ktor.client.call.*
import io.ktor.client.request.*
import util.client
import util.json

class OpenFoodFactsClient private constructor() {

    suspend fun lookupBarcode(barcode : String) : Pair<OpenFoodFactsResponse, Boolean> {
        val response : String = client.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
        println("https://world.openfoodfacts.org/api/v2/product/$barcode.json")
        val parsedResponse = json.decodeFromString<OpenFoodFactsResponse>(response)
        val failed = parsedResponse.status != 0
        return Pair(parsedResponse, failed)
    }

    companion object {

        @Volatile
        private var instance : OpenFoodFactsClient? = null // Volatile modifier is necessary

        fun getInstance() =
            instance ?: synchronized(this) { // synchronized to avoid concurrency problem
                instance ?: OpenFoodFactsClient().also { instance = it }
            }
    }
}