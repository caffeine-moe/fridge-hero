package moe.caffeine.fridgehero.openfoodfacts

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

object OpenFoodFactsApi {

    private var client = HttpClient(CIO)

    suspend fun fetchProductByBarcode(barcode: String): Result<OpenFoodFactsProduct> {
        val clientResponse: String
        try {
            clientResponse = client.get("$BARCODE_API_ENDPOINT/$barcode").bodyAsText()
        } catch (exception: Exception) {
            return Result.failure(exception)
        }
        val openFoodFactsResponse = json.decodeFromString<OpenFoodFactsResponse>(clientResponse)
        return when (openFoodFactsResponse.status) {
            1 -> {
                Result.success(openFoodFactsResponse.product)
            }

            else -> {
                Result.failure(Throwable(openFoodFactsResponse.statusVerbose))
            }
        }
    }
}