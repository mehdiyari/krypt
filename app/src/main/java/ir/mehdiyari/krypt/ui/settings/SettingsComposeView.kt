package ir.mehdiyari.krypt.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsView(
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    val automaticallyLockAppSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    KryptTheme {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.menu_settings))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onNavigationClickIcon()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                )
            }
        ) {
            Row {
                SettingsItems {
                    if (it == R.string.settings_lock_auto) {
                        scope.launch {
                            automaticallyLockAppSheetState.show()
                        }
                    }
                }
            }
        }

        AutomaticallyLockModalBottomSheet(
            automaticallyLockAppSheetState,
            viewModel,
            scope
        ) {
            viewModel.onSelectAutoLockItem(it)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AutomaticallyLockModalBottomSheet(
    automaticallyLockAppSheetState: ModalBottomSheetState,
    viewModel: SettingsViewModel,
    scope: CoroutineScope,
    onItemClicked: (AutoLockItemsEnum) -> Unit = {},
) {
    val lastSelectedId = viewModel.automaticallyLockSelectedItem.collectAsState().value
    ModalBottomSheetLayout(
        sheetState = automaticallyLockAppSheetState,
        sheetContent = {
            AUTO_LOCK_CRYPT_ITEMS.forEach {
                ListItem(
                    modifier = Modifier.selectable(selected = false, onClick = {
                        onItemClicked(it.first)
                        scope.launch {
                            automaticallyLockAppSheetState.hide()
                        }
                    }), text = {
                        if (lastSelectedId == it.first) {
                            Text(
                                stringResource(id = it.second),
                                color = MaterialTheme.colors.primary
                            )
                        } else {
                            Text(stringResource(id = it.second))
                        }
                    }
                )
            }
        }
    ) { }
}

@Composable
fun SettingsItems(
    onItemClick: (Int) -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 85.dp),
    ) {
        items(SETTINGS_LIST) { settingsModel ->
            SettingsItemCard(settingsModel.first, settingsModel.second, onItemClick)
        }
    }
}

@Composable
@Preview
fun SettingsItemCard(
    @DrawableRes iconResId: Int = R.drawable.ic_lock_clock_24,
    @StringRes textResId: Int = R.string.settings_lock_auto,
    onItemClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 8.dp)
            .height(65.dp)
            .selectable(
                selected = false,
                onClick = {
                    onItemClick(textResId)
                }),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = "",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(10.dp, 0.dp, 0.dp, 0.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )

                Text(
                    text = stringResource(id = textResId),
                    modifier = Modifier
                        .padding(8.dp, 0.dp, 4.dp, 0.dp)
                        .align(Alignment.CenterVertically),
                )
            }
        }
    }
}
