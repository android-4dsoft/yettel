package hu.yettel.zg.ui.screens.payment

import androidx.compose.runtime.Composable

@Composable
fun PaymentScreen(
    onSuccessClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
}
