package hu.yettel.zg.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.theme.DarkBlue
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Lime
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme

@Composable
fun YettelTopAppBar(
    title: String,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(Dimens.AppBarHeight)
            .fillMaxWidth()
            .background(Lime, shape = RoundedCornerShape(bottomStart = Dimens.PaddingMedium, bottomEnd = Dimens.PaddingMedium))
            .padding(horizontal = Dimens.PaddingLarge, vertical = Dimens.PaddingSmall),
        contentAlignment = Alignment.BottomStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.clickable { onBackClick() },
            )
            Spacer(Modifier.width(Dimens.PaddingExtraSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DarkBlue,
            )
        }
    }
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
