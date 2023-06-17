package ir.mehdiyari.krypt.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {

    HomeScreen(modifier)

}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

}