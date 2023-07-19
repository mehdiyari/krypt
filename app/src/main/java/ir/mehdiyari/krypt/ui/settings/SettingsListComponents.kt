package ir.mehdiyari.krypt.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme

@Composable
fun SettingItems(
    modifier: Modifier,
    onItemClick: (Int) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 85.dp),
    ) {
        items(SETTINGS_LIST) { settingsModel ->
            SettingsItemCard(
                modifier = modifier,
                settingsModel.first,
                settingsModel.second,
                onItemClick
            )
        }
    }
}

@Composable
fun SettingsItemCard(
    modifier: Modifier,
    @DrawableRes iconResId: Int,
    @StringRes textResId: Int,
    onItemClick: (Int) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 2.dp)
            .height(60.dp)
            .selectable(
                selected = false,
                onClick = {
                    onItemClick(textResId)
                }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "",
                    modifier = modifier
                        .size(45.dp)
                        .padding(10.dp, 0.dp, 0.dp, 0.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )

                Text(
                    text = stringResource(id = textResId),
                    modifier = modifier
                        .padding(8.dp, 0.dp, 4.dp, 0.dp)
                        .align(Alignment.CenterVertically),
                )
            }
        }
    }
}


@Composable
@Preview
fun SettingsItemCardPreview() {
    KryptTheme {
        SettingsItemCard(
            modifier = Modifier,
            iconResId = R.drawable.ic_lock_clock_24,
            textResId = R.string.settings_lock_auto,
            onItemClick = {}
        )
    }
}

@Composable
@Preview
fun SettingItemsPreview() {
    KryptTheme {
        SettingItems(modifier = Modifier, onItemClick = {})
    }
}
