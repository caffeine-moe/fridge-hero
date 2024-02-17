package moe.caffeine.fridgehero.screen.myfridge

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.screen.scanner.ScannerActivity

@Composable
fun MyFridge(barHeight : Dp) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.padding(bottom = barHeight),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, ScannerActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
        ) {
            items(count = 100) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Pork")
                }
            }
        }
    }
}