package moe.caffeine.fridgehero.openfoodfacts

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

object OpenFoodFactsApi {

    private var client = HttpClient(CIO)

    suspend fun fetchProductByBarcode(barcode: String): Result<OpenFoodFactsProduct> {
        val openFoodFactsResponse: OpenFoodFactsResponse
        try {
            val clientResponse = client.get("$BARCODE_API_ENDPOINT/$barcode").bodyAsText()
            openFoodFactsResponse = json.decodeFromString<OpenFoodFactsResponse>(clientResponse)

        } catch (exception: Exception) {
            return Result.failure(exception)
        }
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