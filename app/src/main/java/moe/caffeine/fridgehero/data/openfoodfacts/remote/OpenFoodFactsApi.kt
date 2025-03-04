package moe.caffeine.fridgehero.data.openfoodfacts.remote

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsResponse
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.Constants.BARCODE_API_ENDPOINT
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.Constants.json
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.Constants.trustManager
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object OpenFoodFactsApi {

  data object Constants {
    const val BARCODE_API_ENDPOINT = "https://world.openfoodfacts.org/api/v2/product"

    val trustManager = { certificateStream: InputStream ->
      (TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(KeyStore.getInstance(KeyStore.getDefaultType()).apply {
          load(null, null)
          setCertificateEntry(
            "isrg",
            CertificateFactory.getInstance("X.509").generateCertificate(
              certificateStream
            )
          )
        })
      }).trustManagers.first() as X509TrustManager
    }

    val json = Json { ignoreUnknownKeys = true }

  }

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
