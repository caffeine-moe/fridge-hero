package moe.caffeine.fridgehero.data.remote.openfoodfacts

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import moe.caffeine.fridgehero.data.model.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.OpenFoodFactsResponse
import java.io.InputStream

object OpenFoodFactsApi {

  private fun tryLoadCertificate(): Result<InputStream> =
    try {
      javaClass.classLoader?.getResource("isrg_root_x1.pem")?.openStream()?.let {
        Result.success(it)
      } ?: throw Throwable("classLoader returned null")
    } catch (e: Exception) {
      Result.failure(e)
    }

  private val httpClient: HttpClient by lazy { HttpClient(CIO) }

  private fun getClient(): Result<HttpClient> {
    if (Build.VERSION.SDK_INT >= 25) return Result.success(httpClient)
    var throwable: Throwable? = null
    val client = HttpClient(CIO) {
      tryLoadCertificate().fold(
        onSuccess = {
          engine {
            https {
              trustManager = trustManager(it)
            }
          }
        },
        onFailure = {
          throwable = it
        }
      )
    }
    return throwable?.let { Result.failure(it) } ?: Result.success(client)
  }

  suspend fun fetchProductByBarcode(
    barcode: String,
  ): Result<OpenFoodFactsProduct> = getClient().fold(
    onSuccess = { client ->
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
    },
    onFailure = {
      Result.failure(it)
    }
  )

  suspend fun fetchImageAsByteArrayFromURL(
    url: String
  ): Result<ByteArray> = getClient().fold(
    onSuccess = { client ->
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
    },
    onFailure = {
      Result.failure(it)
    }
  )
}
