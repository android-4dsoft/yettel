package hu.yettel.zg.naivgation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import hu.yettel.zg.ui.screens.highway.HighwayRoute
import hu.yettel.zg.ui.screens.highway.highwayScreen
import hu.yettel.zg.ui.screens.highway.navigateToHighway
import hu.yettel.zg.ui.screens.payment.navigateToPayment
import hu.yettel.zg.ui.screens.payment.paymentScreen
import hu.yettel.zg.ui.screens.success.navigateToSuccess
import hu.yettel.zg.ui.screens.success.successScreen
import hu.yettel.zg.ui.screens.vignettes.navigateToVignettes
import hu.yettel.zg.ui.screens.vignettes.vignettesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("LongMethod", "UnusedParameter")
@Composable
fun YettelNavGraph(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = HighwayRoute.ROUTE,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        highwayScreen(
            onYearlyVignettesClick = { category ->
                navController.navigateToVignettes(
                    category = category,
                )
            },
            onBackClick = {
                (context as? android.app.Activity)?.finish()
            },
            onShowSnackbar = { message, actionLabel ->
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    message = message,
                    actionLabel = actionLabel,
                )
            },
        )

        vignettesScreen(
            onPaymentClick = {
                navController.navigateToPayment()
            },
            onBackClick = {
                navController.popBackStack()
            },
            onShowSnackbar = { message, actionLabel ->
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    message = message,
                    actionLabel = actionLabel,
                )
            },
        )

        paymentScreen(
            onSuccessClick = {
                navController.navigateToSuccess()
            },
            onBackClick = {
                navController.popBackStack()
            },
            onShowSnackbar = { message, actionLabel ->
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    message = message,
                    actionLabel = actionLabel,
                )
            },
        )

        successScreen(
            onDoneClick = {
                // Navigate back to Highway screen but clear the back stack
                navController.navigateToHighway(
                    navOptions = androidx.navigation.navOptions {
                        // Pop up to the start destination of the graph to avoid
                        // a large stack of back destinations
                        popUpTo(HighwayRoute.ROUTE) { inclusive = true }
                    },
                )
            },
        )
    }
}

private suspend fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String,
    actionLabel: String?,
): Boolean {
    var result: SnackbarResult? = null
    coroutineScope
        .launch {
            result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short,
            )
        }.join()
    return result == SnackbarResult.ActionPerformed
}
