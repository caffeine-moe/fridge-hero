package moe.caffeine.fridgehero.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val code: String = "",
    val product: OpenFoodFactsProduct = OpenFoodFactsProduct(),
    val status: Int = 0,
    @SerialName("status_verbose")
    val statusVerbose: String = "",
)