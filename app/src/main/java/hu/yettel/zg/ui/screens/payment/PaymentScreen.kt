package hu.yettel.zg.ui.screens.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.yettel.zg.R
import hu.yettel.zg.domain.model.LocalizedName
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.ui.designsystem.components.ErrorState
import hu.yettel.zg.ui.designsystem.components.LoadingState
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.SecondaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.ui.screens.vignettes.VignetteCounty
import hu.yettel.zg.utils.StringUtil

@Suppress("LongMethod", "UnusedParameter")
@Composable
fun PaymentScreen(
    onSuccessClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: PaymentViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle error messages
    LaunchedEffect(uiState) {
        if (uiState is PaymentUiState.Error) {
            onShowSnackbar((uiState as PaymentUiState.Error).message, null)
        }
    }

    Scaffold(
        topBar = {
            YettelTopAppBar(
                title = stringResource(R.string.module_title),
                onBackClick = onBackClick,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when (uiState) {
            is PaymentUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            is PaymentUiState.Error -> {
                ErrorState(
                    message = (uiState as PaymentUiState.Error).message,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            is PaymentUiState.Success -> {
                val successState = uiState as PaymentUiState.Success
                PaymentContent(
                    state = successState,
                    onSuccessClick = onSuccessClick,
                    onBackClick = onBackClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun PaymentContent(
    state: PaymentUiState.Success,
    onSuccessClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = Dimens.PaddingSmall,
            vertical = Dimens.PaddingLarge,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
    ) {
        item {
            Text(
                text = stringResource(R.string.payment_screen_title),
                style = Typography.headlineMedium.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                        vertical = Dimens.PaddingExtraSmall,
                    ).height(1.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            )

            OrderInfoRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                        vertical = Dimens.PaddingExtraSmall,
                    ),
                start = stringResource(R.string.payment_screen_plate_lbl),
                end = state.vehicle.licensePlate.uppercase(),
            )
            OrderInfoRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                        vertical = Dimens.PaddingExtraSmall,
                    ),
                start = stringResource(R.string.payment_screen_vignette_type_lbl),
                end = stringResource(R.string.yearly_vignette_county_type),
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                        vertical = Dimens.PaddingExtraSmall,
                    ).height(1.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            )
            Spacer(modifier = Modifier.height(Dimens.PaddingExtraSmall))
        }

        // Display selected counties
        items(state.selectedVignettes, key = { vignette -> vignette.county.id }) { vignette ->
            PaymentCountyItem(
                modifier = Modifier
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                    ).fillMaxWidth()
                    .height(Dimens.RowHeightExtraSmall),
                vignetteCounty = vignette.county,
            )
        }

        // Transaction fee row
        item {
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = Dimens.PaddingSmall,
                    ).height(Dimens.RowHeightExtraSmall)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.payment_screen_system_usage_type_lbl),
                    style = Typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(Dimens.PaddingXSmall))
                Text(
                    text = stringResource(
                        R.string.yearly_vignette_price_pl,
                        StringUtil.formatPrice(state.transactionFee),
                    ),
                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                )
            }
        }

        item {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.PaddingExtraSmall,
                        vertical = Dimens.PaddingSmall,
                    ).height(1.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingSmall),
            ) {
                Text(
                    text = stringResource(R.string.amount_lbl),
                    style = Typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )
                Spacer(Modifier.height(Dimens.PaddingXSmall))
                Text(
                    text = stringResource(
                        R.string.yearly_vignette_price_pl,
                        StringUtil.formatPrice(state.totalAmount),
                    ),
                    style = Typography.headlineLarge.copy(
                        fontSize = 40.sp,
                        lineHeight = 48.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
        }

        item {
            Spacer(Modifier.height(Dimens.PaddingSmall))
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.payment_screen_next_btn),
                isEnabled = true,
                onClick = onSuccessClick,
            )
            Spacer(Modifier.height(Dimens.PaddingSmall))
            SecondaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.payment_screen_cancel_btn),
                isEnabled = true,
                onClick = onBackClick,
            )
        }
    }
}

@Composable
fun PaymentCountyItem(
    modifier: Modifier = Modifier,
    vignetteCounty: VignetteCounty,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = vignetteCounty.name,
            style = Typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.secondary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Dimens.PaddingXSmall))
        Text(
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(vignetteCounty.cost)),
            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Composable
fun OrderInfoRow(
    modifier: Modifier = Modifier,
    start: String,
    end: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = start,
            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(weight = 3f),
        )
        Spacer(Modifier.width(Dimens.PaddingSmall))
        Text(
            text = end,
            style = Typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.End,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(2f),
        )
    }
}

@Preview
@Composable
fun PaymentScreenPreview() {
    YettelZGTheme {
        val vehicle = Vehicle(
            internationalCode = "H",
            type = "CAR",
            ownerName = "Michael Scott",
            licensePlate = "ABC 123",
            country = LocalizedName(hungarian = "Magyarország", english = "Hungary"),
            vignetteType = "D1",
        )

        val selectedVignettes = listOf(
            SelectedVignette(
                VignetteCounty(
                    id = "YEAR_16",
                    name = "Fejér",
                    cost = 5450.0,
                    isSelected = true,
                ),
            ),
            SelectedVignette(
                VignetteCounty(
                    id = "YEAR_21",
                    name = "Komárom-Esztergom",
                    cost = 5450.0,
                    isSelected = true,
                ),
            ),
            SelectedVignette(
                VignetteCounty(
                    id = "YEAR_28",
                    name = "Veszprém",
                    cost = 5450.0,
                    isSelected = true,
                ),
            ),
        )

        PaymentContent(
            state = PaymentUiState.Success(
                vehicle = vehicle,
                selectedVignettes = selectedVignettes,
                transactionFee = 110.0,
                totalAmount = 16460.0,
            ),
            onSuccessClick = {},
            onBackClick = {},
        )
    }
}
