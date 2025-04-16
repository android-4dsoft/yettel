@file:Suppress(
    "TooManyFunctions",
)

package hu.yettel.zg.ui.screens.highway

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.yettel.zg.R
import hu.yettel.zg.domain.model.LocalizedName
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.model.VignetteTypeEnum
import hu.yettel.zg.ui.designsystem.components.ErrorState
import hu.yettel.zg.ui.designsystem.components.LoadingState
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelShapes
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.utils.StringUtil

@Composable
fun HighwayScreen(
    onYearlyVignettesClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: HighwayViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is HighwayUiState.Error) {
            onShowSnackbar((uiState as HighwayUiState.Error).message, null)
        }
    }

    Scaffold(
        topBar = {
            YettelTopAppBar(
                title = stringResource(R.string.module_title),
                onBackClick = { /* Handle back navigation if needed */ },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        when (uiState) {
            is HighwayUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is HighwayUiState.Error -> {
                ErrorState(
                    message = (uiState as HighwayUiState.Error).message,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is HighwayUiState.Success -> {
                val successState = uiState as HighwayUiState.Success
                HighwayContent(
                    state = successState,
                    onVignetteTypeSelect = { viewModel.selectVignetteType(it) },
                    onYearlyVignettesClick = onYearlyVignettesClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
        }
    }
}

@Composable
fun HighwayContent(
    state: HighwayUiState.Success,
    onVignetteTypeSelect: (VignetteTypeEnum) -> Unit,
    onYearlyVignettesClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VehicleCard(vehicle = state.vehicle)
        VignetteCard(
            vignetteTypes = state.vignetteTypes,
            selectedVignetteType = state.selectedVignetteType,
            onVignetteTypeSelect = onVignetteTypeSelect,
        )
        if (state.hasYearlyVignette) {
            YearlyVignetteAction(
                onCardClick = {
                    onYearlyVignettesClick(state.vehicle.type)
                },
            )
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle) {
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
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraSmall), // Push top and bottom apart
                ) {
                    Text(
                        text = vehicle.licensePlate.uppercase(),
                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    )
                    Text(
                        text = vehicle.ownerName,
                        style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary),
                    )
                }
            }
        }
    }
}

@Composable
fun VignetteCard(
    vignetteTypes: List<VignetteType>,
    selectedVignetteType: VignetteTypeEnum?,
    onVignetteTypeSelect: (VignetteTypeEnum) -> Unit,
) {
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
            vignetteTypes.forEach { vignetteType ->
                YearlyVignetteItem(
                    isSelected = vignetteType.type == selectedVignetteType,
                    vignetteType = vignetteType,
                    onSelect = { onVignetteTypeSelect(vignetteType.type) },
                )
            }
        }

        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingSmall),
            text = stringResource(R.string.btn_payment_lbl),
            isEnabled = true,
            onClick = {
                // no - op
            },
        )
    }
}

@Composable
fun YearlyVignetteItem(
    isSelected: Boolean,
    vignetteType: VignetteType,
    onSelect: () -> Unit,
) {
    val typeDisplayName = when (vignetteType.type) {
        VignetteTypeEnum.DAY -> stringResource(R.string.yearly_vignette_daily_type)
        VignetteTypeEnum.WEEK -> stringResource(R.string.yearly_vignette_weekly_type)
        VignetteTypeEnum.MONTH -> stringResource(R.string.yearly_vignette_monthly_type)
        else -> ""
    }

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
            onClick = onSelect,
        )
        Spacer(modifier = Modifier.width(Dimens.PaddingSmall))
        Text(
            text = typeDisplayName,
            style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Dimens.PaddingSmall))
        Text(
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(vignetteType.price)),
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
                Dimens.PaddingSmall,
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
            vehicle = Vehicle(
                internationalCode = "H",
                type = "CAR",
                ownerName = "Kovacs Istvan",
                licensePlate = "ABC123",
                country = LocalizedName(
                    hungarian = "Magyarország",
                    english = "Hungary",
                ),
                vignetteType = "D1",
            ),
        )
    }
}

@Preview
@Composable
fun VignetteCardPreview() {
    YettelZGTheme {
        VignetteCard(
            vignetteTypes = listOf(
                VignetteType(
                    type = VignetteTypeEnum.DAY,
                    displayName = "D1",
                    price = 5150.0,
                    isSelected = false,
                ),
                VignetteType(
                    type = VignetteTypeEnum.WEEK,
                    displayName = "D1",
                    price = 6400.0,
                    isSelected = false,
                ),
                VignetteType(
                    type = VignetteTypeEnum.MONTH,
                    displayName = "D1",
                    price = 10360.0,
                    isSelected = true,
                ),
            ),
            selectedVignetteType = VignetteTypeEnum.MONTH,
            onVignetteTypeSelect = {},
        )
    }
}

@Preview
@Composable
fun YearlyVignetteItemPreview() {
    YettelZGTheme {
        YearlyVignetteItem(
            isSelected = true,
            vignetteType = VignetteType(
                type = VignetteTypeEnum.MONTH,
                displayName = "D1",
                price = 10360.0,
                isSelected = true,
            ),
            onSelect = {},
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
            onYearlyVignettesClick = {},
            onShowSnackbar = { _, _ -> false },
        )
    }
}

@Preview(name = "small-screen", device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun HighwayScreenSmallPreview() {
    YettelZGTheme {
        HighwayScreen(
            onYearlyVignettesClick = {},
            onShowSnackbar = { _, _ -> false },
        )
    }
}
