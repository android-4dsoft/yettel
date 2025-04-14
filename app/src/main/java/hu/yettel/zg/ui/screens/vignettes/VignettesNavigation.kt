package hu.yettel.zg.ui.screens.vignettes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object VignettesRoute {
    const val ROUTE = "vignettes"

    override fun toString(): String = ROUTE
}

fun NavController.navigateToVignettes(navOptions: NavOptions? = null) = navigate(route = VignettesRoute.ROUTE, navOptions = navOptions)

fun NavGraphBuilder.vignettesScreen(
    onPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = VignettesRoute.ROUTE) {
        VignettesScreen(
            onPaymentClick = onPaymentClick,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
