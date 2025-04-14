package hu.yettel.zg.ui.screens.highway

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object HighwayRoute {
    const val ROUTE = "highway"

    override fun toString(): String = ROUTE
}

fun NavController.navigateToHighway(navOptions: NavOptions? = null) = navigate(route = HighwayRoute.ROUTE, navOptions = navOptions)

fun NavGraphBuilder.highwayScreen(
    onVignettesClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = HighwayRoute.ROUTE) {
        HighwayScreen(
            onVignettesClick = onVignettesClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
