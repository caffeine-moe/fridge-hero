package moe.caffeine.fridgehero.data.remote.openfoodfacts

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import moe.caffeine.fridgehero.data.model.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.OpenFoodFactsResponse

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

  suspend fun fetchImageAsByteArrayFromURL(url: String): Result<ByteArray> {
    val response: HttpResponse
    try {
      response = client.get(url)
    } catch (exception: Exception) {
      return Result.failure(exception)
    }
    return when (response.status) {
      HttpStatusCode.OK -> {
        Result.success(response.bodyAsBytes())
      }

      else -> Result.failure(Throwable(response.bodyAsText()))
    }
  }
}
