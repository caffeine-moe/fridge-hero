package moe.caffeine.fridgehero.screen.scanner

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LaunchScanner(navHostController : NavHostController) {
    Surface(
        color = MaterialTheme.colorScheme.background,
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

            LaunchedEffect(cameraPermissionState) {
                when {
                    !cameraPermissionState.status.isGranted -> {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            }

            CameraPreview(navHostController)
        }
    }
}

@Composable
fun CameraPreview(navHostController : NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }

    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { previewView ->
        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        val cameraExecutor : ExecutorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture : ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()
            val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                barcodes.forEach { barcode ->
                    barcode.rawValue?.let { barcodeValue ->
                        barCodeVal.value = barcodeValue

                        cameraProvider.unbindAll()

                        val foodItem = runBlocking { ProcessItem(barcodeValue) }

                        when (foodItem) {
                            null -> {
                                Toast.makeText(context, "Unable to add $barcodeValue to fridge.", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            else -> {
                                Toast.makeText(context, "Added ${foodItem.name} to fridge.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        previewView.removeAllViews()

                        navHostController.navigate(
                            "My Fridge",
                            navOptions = NavOptions.Builder().setPopUpTo("My Fridge", true).build()
                        )
                    }
                }
            }
            val imageAnalysis : ImageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e : Exception) {
                Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
            }
        }, ContextCompat.getMainExecutor(context))
    }
}