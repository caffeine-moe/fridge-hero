package moe.caffeine.fridgehero.data.openfoodfacts.remote

import kotlinx.serialization.json.Json
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

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
