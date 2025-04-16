package hu.yettel.zg.ui.screens.success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.yettel.zg.R
import hu.yettel.zg.ui.designsystem.components.PrimaryButton
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.Typography
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme

@Composable
fun SuccessScreen(onDoneClick: () -> Unit) {
    BackHandler(enabled = true) {
        // No-op: Back press is intercepted and does nothing
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Image(
            painter = painterResource(R.drawable.confetti),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = stringResource(R.string.success_screen_msg_lbl),
                style = Typography.headlineLarge.copy(
                    fontSize = 40.sp,
                    lineHeight = 48.sp,
                    color = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .padding(
                        Dimens.PaddingLarge,
                    ),
            )

            Image(
                painter = painterResource(R.drawable.walking_man),
                contentDescription = null,
                modifier = Modifier
                    .heightIn(min = 293.dp)
                    .widthIn(min = 281.dp)
                    .align(Alignment.End)
                    .offset(x = 40.dp),
                contentScale = ContentScale.Fit,
            )
            // Bottom button
            PrimaryButton(
                text = stringResource(R.string.success_screen_done_btn),
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        Dimens.PaddingLarge,
                    ),
            )
        }
    }
}

@Preview(name = "small-screen", device = "spec:width=360dp,height=640dp,dpi=480")
@Composable
fun SuccessScreenSmallPreview() {
    YettelZGTheme {
        SuccessScreen { }
    }
}

@Preview()
@Composable
fun SuccessScreenNormalPreview() {
    YettelZGTheme {
        SuccessScreen { }
    }
}
