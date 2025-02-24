package moe.caffeine.fridgehero.ui.overlay

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.component.scanner.Scanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerOverlay(
  barcodeScanRequest: Event.RequestBarcodeFromScanner?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(
    barcodeScanRequest != null,
    enter = slideInVertically(
      animationSpec = tween(500)
    ) + fadeIn(tween(250)),
    exit = slideOutVertically(
      animationSpec = tween(500)
    ) + fadeOut(tween(250))
  ) {
    if (barcodeScanRequest == null) return@AnimatedVisibility
    val backColour = MaterialTheme.colorScheme.surface
    Scaffold(
      modifier = Modifier
        .systemBarsPadding(),
      topBar = {
        TopAppBar({
          Row(Modifier.fillMaxWidth()) {
            Box(
              Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              TextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onDismiss
              ) {
                Text("Back")
              }
              Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Scanner"
              )
            }
          }
        })
      }
    ) { paddingValues ->
      var cameraReady by remember { mutableStateOf(false) }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        BackHandler(enabled = true) {
          onDismiss()
        }
        val swapDimensions = LocalConfiguration.current.orientation == 2
        Box(Modifier.fillMaxSize()) {
          Box(
            modifier = Modifier
              .drawWithContent {
                val physicalDimensions = arrayOf(
                  size.width,
                  size.height
                ).also { dimensions ->
                  if (swapDimensions)
                    dimensions.reverse()
                }
                //calculate dimensions of indicative rectangle as proportion of screen dimensions
                val boxDimensions = Size(
                  physicalDimensions[0] * 0.9f,
                  physicalDimensions[1] * 0.3f
                )

                drawContent()
                //overlay on top of camera feed
                drawRect(backColour.copy(alpha = 0.7f))

                val cutoutTopLeft = Offset(
                  (size.width - boxDimensions.width) / 2,
                  (size.height - boxDimensions.height) / 2
                )

                //draw transparent cutout
                drawRoundRect(
                  topLeft = cutoutTopLeft,
                  size = boxDimensions,
                  color = Color.Transparent,
                  cornerRadius = CornerRadius(24.dp.toPx()),
                  blendMode = BlendMode.SrcIn
                )

                //draw white rectangle around cutout
                drawRoundRect(
                  topLeft = cutoutTopLeft,
                  color = Color.White,
                  size = boxDimensions,
                  cornerRadius = CornerRadius(24.dp.toPx()),
                  style = Stroke(
                    width = 2.dp.toPx()
                  ),
                  blendMode = BlendMode.Src
                )
              }
              .fillMaxSize()
          ) {
            Scanner(
              onCameraReady = { cameraReady = true },
              onDismiss = onDismiss,
            ) {
              barcodeScanRequest.result.complete(Result.success(it))
              onDismiss()
            }
          }
          AnimatedVisibility(
            visible = !cameraReady,
            enter = EnterTransition.None,
            exit = slideOutHorizontally(
              tween(500, delayMillis = 250), targetOffsetX = { -it }
            ),
            modifier = Modifier.fillMaxSize()
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(backColour)
            )
          }
        }
      }
    }
  }
}
