package moe.caffeine.fridgehero.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.recipe.persistentRecipes
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun AvailableRecipes() {
    val scrollState = rememberScrollState()
    Text(
        modifier = Modifier.padding(top = 10.dp),
        style = Typography.titleMedium,
        text = "Recipes"
    )
    Row(
        modifier = Modifier.horizontalScroll(scrollState)
    ) {
        persistentRecipes.forEach { recipe ->
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
                    text = recipe
                )
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}