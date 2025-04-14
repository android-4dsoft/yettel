package hu.yettel.zg.ui.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.yettel.zg.ui.designsystem.theme.DarkBlue
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.White
import hu.yettel.zg.ui.designsystem.theme.YettelShapes
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.ButtonHeight),
        shape = YettelShapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkBlue,
            contentColor = White,
        ),
        contentPadding = PaddingValues(Dimens.ContentPadding),
        elevation = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.ButtonHeight),
        shape = YettelShapes.large,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DarkBlue,
        ),
        border = BorderStroke(Dimens.StrokeWidth, DarkBlue),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    YettelZGTheme {
        PrimaryButton(
            text = "Button",
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}

@Preview
@Composable
fun SecondaryButtonPreview() {
    YettelZGTheme {
        SecondaryButton(
            text = "Button",
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}
