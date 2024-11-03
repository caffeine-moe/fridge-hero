package moe.caffeine.fridgehero.openfoodfacts

import kotlinx.serialization.json.Json

const val BARCODE_API_ENDPOINT = "https://world.openfoodfacts.org/api/v2/product"

val json = Json { ignoreUnknownKeys = true }