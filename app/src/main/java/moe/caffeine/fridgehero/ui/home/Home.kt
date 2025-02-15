package moe.caffeine.fridgehero.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.home.components.Greeting

@Composable
fun Home(profile: Profile) {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .padding(10.dp)
      .verticalScroll(scrollState)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    Greeting(profile.firstName)
    //ExpiringSoon()
    //AvailableRecipes()
  }
}
