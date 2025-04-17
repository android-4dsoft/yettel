@file:Suppress("MatchingDeclarationName", "MaxLineLength")

package hu.yettel.zg.ui.screens.vignettes

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable

@Serializable
object VignettesRoute {
    const val NAV_ARGUMENT = "category"
    const val ROUTE = "vignettes/{$NAV_ARGUMENT}"
    val args = listOf(
        navArgument(NAV_ARGUMENT) {
            type = NavType.StringType
        },
    )

    override fun toString(): String = ROUTE
}

fun NavController.navigateToVignettes(
    category: String,
    navOptions: NavOptions? = null,
) {
    navigate(route = "vignettes/$category", navOptions)
}

fun NavGraphBuilder.vignettesScreen(
    onPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = VignettesRoute.ROUTE, arguments = VignettesRoute.args) {
        VignettesScreen(
            onPaymentClick = onPaymentClick,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
