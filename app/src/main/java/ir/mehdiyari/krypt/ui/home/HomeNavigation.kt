package ir.mehdiyari.krypt.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_HOME = "home"

fun NavController.navigateToHome(){
    this.navigate(ROUTE_HOME)
}

fun NavGraphBuilder.homeScreen(){
    composable(ROUTE_HOME){

    }
}