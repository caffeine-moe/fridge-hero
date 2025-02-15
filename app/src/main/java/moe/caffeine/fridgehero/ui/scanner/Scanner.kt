package moe.caffeine.fridgehero.ui.scanner

import android.Manifest
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Scanner(
  paddingValues: PaddingValues = PaddingValues(),
  onDismiss: () -> Unit,
  onScanned: (String) -> Unit,
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val context = LocalContext.current
  val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
  var preview: Preview? by remember { mutableStateOf(null) }
  var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
  var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }

  //clean up when leaving composition
  DisposableEffect(Unit) {
    onDispose {
      cameraProvider?.unbindAll()
      cameraExecutor?.shutdown()
    }
  }

  //get camera permissions
  LaunchedEffect(cameraPermissionState) {
    when {
      !cameraPermissionState.status.isGranted -> {
        cameraPermissionState.launchPermissionRequest()
      }
    }
  }

  // Create a composable view for the camera feed
  AndroidView(
    factory = { androidViewContext ->
      PreviewView(androidViewContext).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
      }
    },
    modifier = Modifier
      .fillMaxSize()
      .consumeWindowInsets(paddingValues),
    // When the view is (re)composed
    update = { previewView ->
      val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

      cameraProviderFuture.addListener({

        //get the back camera
        cameraProvider = cameraProviderFuture.get()
        cameraExecutor = Executors.newSingleThreadExecutor()

        preview = Preview.Builder().build().also {
          it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector: CameraSelector = CameraSelector.Builder()
          .requireLensFacing(CameraSelector.LENS_FACING_BACK)
          .build()

        //prepare the barcode scanning
        val barcodeAnalyser = BarcodeAnalyser { barcode ->
          onScanned(barcode)
        }
        val imageAnalysis = ImageAnalysis.Builder()
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build()
          .also {
            it.setAnalyzer(cameraExecutor!!, barcodeAnalyser)
          }

        //bind to the camera, start scanning
        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
          lifecycleOwner,
          cameraSelector,
          preview,
          imageAnalysis
        )
      }, ContextCompat.getMainExecutor(context))
    }
  )
}
