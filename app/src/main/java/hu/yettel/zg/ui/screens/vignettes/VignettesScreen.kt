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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.components.YettelTopAppBar
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme
import hu.yettel.zg.utils.StringUtil

@Composable
fun VignettesScreen(
    onPaymentClick: () -> Unit,
    onBackClick: () -> Unit,
    showWarning: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val selectedCounties = remember { mutableStateListOf<County>() }

    fun toggleCountySelection(county: County) {
        if (selectedCounties.isNotEmpty() && !selectedCounties.contains(county)) {
            val isAdjacent = isCountyAdjacentToSelected(
                county.id,
                selectedCounties.map { it.id },
                getCountyAdjacencyMap(),
            )

            if (!isAdjacent) {
                showWarning("A kiválasztott vármegyék nem határosak egymással.")
                return
            }
        }

        if (selectedCounties.any { it.id == county.id }) {
            selectedCounties.removeIf { it.id == county.id }
        } else {
            selectedCounties.add(county)
        }
    }

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
                    counties = counties,
                    selectedCounties = selectedCounties.toList(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingSmall),
                )
            }

            items(
                counties.filter { it.id.startsWith("YEAR_") },
                key = { it.id },
            ) { county ->
                VignetteCountyItem(
                    modifier = Modifier
                        .padding(horizontal = Dimens.PaddingSmall)
                        .fillMaxWidth()
                        .height(Dimens.RowHeightExtraSmall),
                    county = county,
                    onCheckStateChanged = {
                        toggleCountySelection(county)
                    },
                    isChecked = selectedCounties.contains(county),
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
                    text = stringResource(R.string.vignettes_screen_next_btn),
                    isEnabled = true,
                    onClick = onPaymentClick,
                )
            }
        }
    }
}

@Composable
fun VignetteCountyItem(
    modifier: Modifier = Modifier,
    county: County,
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
            onCheckedChange = {}, // no-op to avoid double toggle
        )
        Spacer(Modifier.width(Dimens.PaddingXSmall))
        Text(
            text = county.name,
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
            text = stringResource(R.string.yearly_vignette_price_pl, StringUtil.formatPrice(county.cost)),
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
    counties: List<County>,
    selectedCounties: List<County>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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

/**
 * Get the resource ID for a county's vector drawable
 */
private fun getCountyVectorRes(countyId: String): Int =
    when (countyId) {
        "YEAR_11" -> R.drawable.year_11
        "YEAR_12" -> R.drawable.year_12
        "YEAR_13" -> R.drawable.year_13
        "YEAR_14" -> R.drawable.year_14
        "YEAR_15" -> R.drawable.year_15
        "YEAR_16" -> R.drawable.year_16
        "YEAR_17" -> R.drawable.year_17
        "YEAR_18" -> R.drawable.year_18
        "YEAR_19" -> R.drawable.year_19
        "YEAR_20" -> R.drawable.year_20
        "YEAR_21" -> R.drawable.year_21
        "YEAR_22" -> R.drawable.year_22
        "YEAR_23" -> R.drawable.year_23
        "YEAR_24" -> R.drawable.year_24
        "YEAR_25" -> R.drawable.year_25
        "YEAR_26" -> R.drawable.year_26
        "YEAR_27" -> R.drawable.year_27
        "YEAR_28" -> R.drawable.year_28
        "YEAR_29" -> R.drawable.year_29
        else -> R.drawable.bp
    }

@Preview
@Composable
fun HungaryMapPreview() {
    YettelZGTheme {
        Surface {
            val selectedCounties = listOf(
                County("YEAR_11", "Bács-Kiskun"),
                County("YEAR_16", "Fejér"),
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
            county = counties.last(),
            isChecked = true,
            onCheckStateChanged = {},
        )
    }
}

@Preview
@Composable
fun VignettesScreenPreview() {
    YettelZGTheme {
        VignettesScreen(
            onPaymentClick = {},
            onBackClick = {},
            showWarning = {},
            onShowSnackbar = { _, _ -> false },
        )
    }
}
