@file:Suppress(
    "TooManyFunctions",
)

package hu.yettel.zg.ui.screens.highway

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelShapes
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.utils.StringUtil

@Suppress("UnusedParameter")
@Composable
fun HighwayScreen(
    onVignettesClick: () -> Unit,
    onYearlyVignettesClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    Scaffold(
        topBar = {
            YettelTopAppBar(
                title = stringResource(R.string.module_title),
                onBackClick = { /* Handle back navigation if needed */ },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VehicleCard(
                plate = "ABC123",
                owner = "Kovacs Istvan",
            )
            VignetteCard()
            YearlyVignetteAction(
                onCardClick = onYearlyVignettesClick,
            )
        }
    }
}

@Composable
fun VehicleCard(
    plate: String,
    owner: String,
) {
    Card(
        modifier = Modifier
            .padding(
                start = Dimens.PaddingSmall,
                end = Dimens.PaddingSmall,
                top = Dimens.PaddingSmall,
            ).fillMaxWidth()
            .height(Dimens.CardHeight),
        shape = YettelShapes.medium,
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = Dimens.PaddingLarge,
                        end = Dimens.PaddingLarge,
                        top = Dimens.PaddingSmall,
                        bottom = Dimens.PaddingExtraSmall,
                    ).height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.car),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(Dimens.IconSizeMedium),
                )
                Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    // .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall), // Push top and bottom apart
                ) {
                    Text(
                        text = plate,
                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    )
                    Text(
                        text = owner,
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary),
                    )
                }
            }
        }
    }
}

@Composable
fun VignetteCard() {
    Card(
        modifier = Modifier
            .padding(
                start = Dimens.PaddingSmall,
                end = Dimens.PaddingSmall,
                top = Dimens.PaddingSmall,
            ).fillMaxWidth(),
        shape = YettelShapes.medium,
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
    ) {
        Text(
            text = stringResource(R.string.yearly_vignettes_title),
            style = Typography.headlineMedium.copy(color = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.padding(Dimens.PaddingSmall),
        )
        Column(
            modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall),
        ) {
            YearlyVignetteItem(
                isSelected = true,
                type = stringResource(R.string.yearly_vignette_monthly_type),
                price = stringResource(R.string.yearly_vignette_monthly_price).toDouble(),
            )
            YearlyVignetteItem(
                isSelected = false,
                type = stringResource(R.string.yearly_vignette_weekly_type),
                price = stringResource(R.string.yearly_vignette_weekly_price).toDouble(),
            )
            YearlyVignetteItem(
                isSelected = false,
                type = stringResource(R.string.yearly_vignette_daily_type),
                price = stringResource(R.string.yearly_vignette_daily_price).toDouble(),
            )
        }

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingSmall),
            text = stringResource(R.string.btn_payment_lbl),
            isEnabled = true,
            onClick = {},
        )
    }
}

@Composable
fun YearlyVignetteItem(
    isSelected: Boolean,
    type: String,
    price: Double,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.RowHeightMedium)
            .border(
                shape = YettelShapes.small,
                border = BorderStroke(
                    Dimens.StrokeWidth,
                    if (isSelected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                ),
            ).padding(horizontal = Dimens.PaddingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomRadioButton(
            selected = isSelected,
            onClick = {},
        )
        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        Text(
            text = type,
            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Dimens.PaddingSmall))
        Text(
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(price)),
            style = Typography.headlineSmall.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (selected) R.drawable.radiobtn_on else R.drawable.radiobtn_off
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        modifier = modifier
            .size(Dimens.IconSizeLarge)
            .clickable { onClick() },
        tint = Color.Unspecified,
    )
}

@Composable
fun YearlyVignetteAction(onCardClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(
                start = Dimens.PaddingSmall,
                end = Dimens.PaddingSmall,
                top = Dimens.PaddingSmall,
            ).fillMaxWidth()
            .height(Dimens.CardHeight)
            .clickable {
                onCardClick()
            },
        shape = YettelShapes.medium,
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.yearly_vignette_action_lbl),
                style = Typography.headlineMedium.copy(color = MaterialTheme.colorScheme.secondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(Dimens.PaddingSmall))
            Icon(
                painter = painterResource(R.drawable.chevron),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(Dimens.IconSizeMedium),
            )
        }
    }
}

@Preview
@Composable
fun VehicleCardPreview() {
    YettelZGTheme {
        VehicleCard(
            plate = "ABC123",
            owner = "Kovacs Istvan",
        )
    }
}

@Preview
@Composable
fun VignetteCardPreview() {
    YettelZGTheme {
        VignetteCard()
    }
}

@Preview
@Composable
fun YearlyVignetteItemPreview() {
    YettelZGTheme {
        YearlyVignetteItem(
            isSelected = true,
            type = stringResource(R.string.yearly_vignette_monthly_type),
            price = stringResource(R.string.yearly_vignette_monthly_price).toDouble(),
        )
    }
}

@Preview
@Composable
fun YearlyVignetteActionPreview() {
    YettelZGTheme {
        YearlyVignetteAction(
            onCardClick = {},
        )
    }
}

@Preview
@Composable
fun HighwayScreenPreview() {
    YettelZGTheme {
        HighwayScreen(
            onVignettesClick = {},
            onYearlyVignettesClick = {},
            onShowSnackbar = { _, _ -> false },
        )
    }
}
