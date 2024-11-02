package moe.caffeine.fridgehero.scanner

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScanner(
    barcode: (String) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(cameraPermissionState) {
        when {
            !cameraPermissionState.status.isGranted -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    AndroidView(
        factory = { androidViewContext -> PreviewView(androidViewContext) },
        modifier = Modifier.fillMaxSize()
    ) { previewView ->
        println(previewView.bitmap)
    }
}