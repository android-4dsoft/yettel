package hu.yettel.zg.ui.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.theme.DarkBlue
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YettelTopAppBar(
    title: String,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = Dimens.PaddingMedium,
                    bottomEnd = Dimens.PaddingMedium,
                ),
            ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = DarkBlue,
            navigationIconContentColor = DarkBlue,
        ),
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = Dimens.PaddingMedium,
                        end = Dimens.PaddingExtraSmall,
                    ).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        onBackClick()
                    },
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DarkBlue,
            )
        },
    )
}

@Preview
@Composable
fun YettelTopAppBarPreview() {
    YettelZGTheme {
        YettelTopAppBar(
            title = stringResource(R.string.module_title),
            onBackClick = {},
        )
    }
}
