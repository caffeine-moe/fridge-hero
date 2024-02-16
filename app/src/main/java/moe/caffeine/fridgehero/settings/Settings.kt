package moe.caffeine.fridgehero.settings

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.alorma.compose.settings.ui.SettingsCheckbox
import kotlin.system.exitProcess

@Composable
fun Settings() {
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsCheckbox(
                icon = { Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wifi") },
                title = { Text(text = "Hello") },
                subtitle = { Text(text = "This is a longer text") },
                onCheckedChange = { newValue -> },
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Settings")
            Button(onClick = {
                context.dataDir.listFiles()?.filter { it.name.contains(".json") }?.map { it.absoluteFile.delete() }
            }){
                Text("Reset")
            }
        }
    }
}