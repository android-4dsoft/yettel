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
import hu.yettel.zg.ui.designsystem.theme.Dimens
import hu.yettel.zg.ui.designsystem.theme.YettelShapes
import hu.yettel.zg.ui.designsystem.theme.YettelZGTheme

@Composable
fun PrimaryButton(
    text: String,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.ButtonHeight),
        shape = YettelShapes.large,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
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
    isEnabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.ButtonHeight),
        shape = YettelShapes.large,
        enabled = isEnabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            Dimens.StrokeWidth,
            if (isEnabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(name = "enabled button")
@Composable
fun PrimaryEnabledButtonPreview() {
    YettelZGTheme {
        PrimaryButton(
            text = "Button",
            isEnabled = true,
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}

@Preview(name = "disabled button")
@Composable
fun PrimaryDisabledButtonPreview() {
    YettelZGTheme {
        PrimaryButton(
            text = "Button",
            isEnabled = false,
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}

@Preview(name = "enabled outline button")
@Composable
fun SecondaryEnabledButtonPreview() {
    YettelZGTheme {
        SecondaryButton(
            text = "Button",
            isEnabled = true,
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}

@Preview(name = "disabled outline button")
@Composable
fun SecondaryDisabledButtonPreview() {
    YettelZGTheme {
        SecondaryButton(
            text = "Button",
            isEnabled = false,
            onClick = {},
            modifier = Modifier.width(200.dp),
        )
    }
}
