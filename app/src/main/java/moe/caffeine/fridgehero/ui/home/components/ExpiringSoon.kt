package moe.caffeine.fridgehero.ui.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun ExpiringSoon() {
  val scrollState = rememberScrollState()
  Text(
    modifier = Modifier.padding(top = 10.dp),
    style = Typography.titleMedium,
    text = "Expiring Soon"
  )
  Row(
    modifier = Modifier.horizontalScroll(scrollState)
  ) {
/*        persistentFridge.forEach { item ->
            Card(
                modifier = Modifier.padding(10.dp),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp),
                        contentScale = ContentScale.FillBounds,
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "test"
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = item
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = Typography.bodyMedium,
                    text = "Expires: Today"
                )
            }
        }*/
  }
}
