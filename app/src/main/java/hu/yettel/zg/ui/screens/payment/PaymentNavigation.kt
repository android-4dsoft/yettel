@file:Suppress("MatchingDeclarationName", "MaxLineLength")

package hu.yettel.zg.ui.screens.payment

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object PaymentRoute {
    const val ROUTE = "payment"

    override fun toString(): String = ROUTE
}

fun NavController.navigateToPayment(navOptions: NavOptions? = null) = navigate(route = PaymentRoute.ROUTE, navOptions = navOptions)

fun NavGraphBuilder.paymentScreen(
    onSuccessClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = PaymentRoute.ROUTE) {
        val viewModel: PaymentViewModel = hiltViewModel()
        PaymentScreen(
            onPaymentClick = { coroutineScope ->
                coroutineScope.launch {
                    val success = viewModel.placeOrder()
                    if (success) {
                        viewModel.clearSelections()
                        onSuccessClick()
                    } else {
                        onShowSnackbar("Failed to place order. Please try again.", null)
                    }
                }
            },
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
            viewModel = viewModel,
        )
    }
}
