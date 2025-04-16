package hu.yettel.zg.ui.screens.vignettes

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.components.ErrorState
import hu.yettel.zg.ui.designsystem.components.LoadingState
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.utils.StringUtil
import kotlinx.coroutines.launch

@Suppress("LongMethod", "UnusedParameter")
@Composable
fun VignettesScreen(
    onPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: VignettesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val notAdjacentWarning = stringResource(R.string.error_not_adjacent)

    // Handle error messages
    LaunchedEffect(uiState) {
        if (uiState is VignettesUiState.Error) {
            onShowSnackbar((uiState as VignettesUiState.Error).message, null)
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
            is VignettesUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is VignettesUiState.Error -> {
                ErrorState(
                    message = (uiState as VignettesUiState.Error).message,
                    onRetry = { /* Could add retry functionality */ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }
            is VignettesUiState.Success -> {
                val successState = uiState as VignettesUiState.Success
                VignettesContent(
                    state = successState,
                    onCountyToggle = { countyId ->
                        viewModel.toggleCountySelection(countyId)
                    },
                    onPaymentClick = onPaymentClick,
                    onShowNotAdjacentWarning = {
                        coroutineScope.launch {
                            onShowSnackbar(notAdjacentWarning, null)
                        }
                    },
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
fun VignettesContent(
    state: VignettesUiState.Success,
    onCountyToggle: (String) -> Unit,
    onPaymentClick: () -> Unit,
    onShowNotAdjacentWarning: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = Dimens.PaddingSmall,
            vertical = Dimens.PaddingMedium,
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
    ) {
        item {
            Text(
                text = stringResource(R.string.vignettes_screen_title),
                style = Typography.headlineMedium.copy(color = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.padding(horizontal = Dimens.PaddingSmall),
            )
        }

        item {
            HungaryMap(
                counties = state.counties,
                selectedCounties = state.selectedCounties,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingSmall),
            )
        }

        items(
            state.counties.filter { it.id.startsWith("YEAR_") || it.id == "BP" },
            key = { it.id },
        ) { county ->
            VignetteCountyItem(
                modifier = Modifier
                    .padding(horizontal = Dimens.PaddingSmall)
                    .fillMaxWidth()
                    .height(Dimens.RowHeightExtraSmall),
                vignetteCounty = county,
                onCheckStateChanged = {
                    // Check if we need to handle adjacency warning
                    if (!county.isSelected && state.selectedCounties.isNotEmpty()) {
                        val isAdjacent = isCountyAdjacentToSelected(
                            county.id,
                            state.selectedCounties.map { it.id },
                            getCountyAdjacencyMap(),
                        )

                        if (!isAdjacent) {
                            onShowNotAdjacentWarning()
                            return@VignetteCountyItem
                        }
                    }

                    onCountyToggle(county.id)
                },
                isChecked = county.isSelected,
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingSmall),
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                )
                Spacer(Modifier.height(Dimens.PaddingMedium))
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
                        StringUtil.formatPrice(state.totalCost),
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
            Spacer(Modifier.height(Dimens.PaddingLarge))
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(R.string.vignettes_screen_next_btn),
                isEnabled = state.selectedCounties.isNotEmpty(),
                onClick = onPaymentClick,
            )
        }
    }
}

@Composable
fun VignetteCountyItem(
    modifier: Modifier = Modifier,
    vignetteCounty: VignetteCounty,
    isChecked: Boolean,
    onCheckStateChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onCheckStateChanged(!isChecked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VignetteItemCheckbox(
            modifier = Modifier,
            checked = isChecked,
            onCheckedChange = {
                onCheckStateChanged(!isChecked)
            },
        )
        Spacer(Modifier.width(Dimens.PaddingXSmall))
        Text(
            text = vignetteCounty.name,
            style = Typography.bodyLarge.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.secondary,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(Dimens.PaddingXSmall))
        Text(
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(vignetteCounty.cost)),
            style = Typography.headlineSmall.copy(color = MaterialTheme.colorScheme.secondary),
        )
    }
}

@Composable
fun VignetteItemCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (checked) R.drawable.cb_checked else R.drawable.cb_unchecked
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        modifier = modifier
            .size(Dimens.IconSizeSmall)
            .clickable { onCheckedChange(!checked) },
        tint = Color.Unspecified,
    )
}

@Composable
fun HungaryMap(
    counties: List<VignetteCounty>,
    selectedCounties: List<VignetteCounty>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    @Suppress("MagicNumber")
    val mapAspectRatio = 313f / 188f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(mapAspectRatio),
    ) {
        counties.forEach { county ->
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                },
                update = { imageView ->
                    // Get the county drawable
                    val drawable = ContextCompat.getDrawable(
                        context,
                        getCountyVectorRes(county.id),
                    )

                    // Just set the selection state on the ImageView
                    imageView.isSelected = selectedCounties.contains(county)
                    imageView.setImageDrawable(drawable)
                },
            )
        }
    }
}

@Preview
@Composable
fun HungaryMapPreview() {
    YettelZGTheme {
        Surface {
            val selectedCounties = listOf(
                VignetteCounty("YEAR_11", "Bács-Kiskun"),
                VignetteCounty("YEAR_16", "Fejér"),
            )

            HungaryMap(
                counties = counties,
                selectedCounties = selectedCounties,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
fun VignetteCountyItemPreview() {
    YettelZGTheme {
        VignetteCountyItem(
            modifier = Modifier
                .height(Dimens.RowHeightExtraSmall)
                .fillMaxWidth(),
            vignetteCounty = counties.last(),
            isChecked = true,
            onCheckStateChanged = {},
        )
    }
}

@Preview
@Composable
fun VignettesContentPreview() {
    YettelZGTheme {
        val sampleCounties = listOf(
            VignetteCounty(
                id = "BP",
                name = "Budapest",
                cost = 5450.0,
                isSelected = true,
            ),
            VignetteCounty(
                id = "YEAR_11",
                name = "Bács-Kiskun",
                cost = 5450.0,
                isSelected = false,
            ),
            VignetteCounty(
                id = "YEAR_16",
                name = "Fejér",
                cost = 5450.0,
                isSelected = true,
            ),
            VignetteCounty(
                id = "YEAR_23",
                name = "Pest",
                cost = 5450.0,
                isSelected = false,
            ),
        )

        val selectedCounties = sampleCounties.filter { it.isSelected }

        Surface {
            VignettesContent(
                state = VignettesUiState.Success(
                    counties = sampleCounties,
                    selectedCounties = selectedCounties,
                    totalCost = 10900.0,
                ),
                onCountyToggle = {},
                onPaymentClick = {},
                onShowNotAdjacentWarning = {},
                modifier = Modifier.padding(0.dp),
            )
        }
    }
}
