@file:Suppress("MatchingDeclarationName")
package hu.yettel.zg.ui.screens.success

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SuccessRoute {
    const val ROUTE = "success"

    override fun toString(): String = ROUTE
}

fun NavController.navigateToSuccess(navOptions: NavOptions? = null) =
    navigate(route = SuccessRoute.ROUTE, navOptions = navOptions)

fun NavGraphBuilder.successScreen(onDoneClick: () -> Unit) {
    composable(route = SuccessRoute.ROUTE) {
        SuccessScreen(
            onDoneClick = onDoneClick,
        )
    }
}
