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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.SecondaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.ui.screens.vignettes.County
import hu.yettel.zg.ui.screens.vignettes.counties
import hu.yettel.zg.utils.StringUtil

@Composable
fun PaymentScreen(
    onSuccessClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    Scaffold(
        topBar = {
            YettelTopAppBar(
                title = stringResource(R.string.module_title),
                onBackClick = onBackClick,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                    end = "ABC 123",
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
            }
            items(counties, key = { county -> county.id }) { county ->
                PaymentCountyItem(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimens.PaddingSmall,
                        ).fillMaxWidth()
                        .height(Dimens.RowHeightExtraSmall),
                    county = county,
                )
            }
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
                        text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(stringResource(R.string.payment_screen_system_usage_price_lbl).toDouble())),
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
                            vertical = Dimens.PaddingLarge,
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
                        text = "123 000 Ft",
                        style = Typography.headlineLarge.copy(
                            fontSize = 40.sp,
                            lineHeight = 48.sp,
                            color = MaterialTheme.colorScheme.secondary,
                        ),
                    )
                }
            }
            item {
                Spacer(Modifier.height(Dimens.PaddingLarge))
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
}

@Composable
fun PaymentCountyItem(
    modifier: Modifier = Modifier,
    county: County,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = county.name,
            style = Typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.secondary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Dimens.PaddingXSmall))
        Text(
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(county.cost)),
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
            modifier = Modifier.weight(3f),
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
        PaymentScreen(
            onSuccessClick = {},
            onBackClick = {},
            onShowSnackbar = { _, _ -> false },
        )
    }
}
