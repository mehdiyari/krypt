package ir.mehdiyari.krypt.ui.settings

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.PasswordTextField
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    val automaticallyLockAppSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val deleteDialogState = remember { mutableStateOf(false) }
    KryptTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val deleteAccountViewState = viewModel.deleteAccountState.collectAsState()
            when (deleteAccountViewState.value) {
                DeleteAccountViewState.DeleteAccountFailed -> {
                    deleteDialogState.value = false
                    Toast.makeText(
                        LocalContext.current,
                        R.string.account_delete_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
                DeleteAccountViewState.DeleteAccountFinished -> {
                    deleteDialogState.value = false
                    Toast.makeText(
                        LocalContext.current,
                        R.string.your_account_deleted,
                        Toast.LENGTH_LONG
                    ).show()
                }
                DeleteAccountViewState.DeleteAccountStarts -> {
                    deleteDialogState.value = false
                    CircularProgressIndicator(modifier = Modifier.size(80.dp))
                }
                DeleteAccountViewState.PasswordsNotMatch, null -> {
                    if (deleteAccountViewState.value == DeleteAccountViewState.PasswordsNotMatch) {
                        Toast.makeText(
                            LocalContext.current,
                            R.string.password_not_match,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    SettingItems(
                        viewModel,
                        deleteDialogState,
                        automaticallyLockAppSheetState,
                        scope,
                        onNavigationClickIcon
                    )
                }
            }
        }

        AutomaticallyLockModalBottomSheet(
            automaticallyLockAppSheetState,
            viewModel.automaticallyLockSelectedItem,
            scope
        ) {
            viewModel.onSelectAutoLockItem(it)
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItems(
    viewModel: SettingsViewModel,
    deleteDialogState: MutableState<Boolean>,
    automaticallyLockAppSheetState: SheetState,
    scope: CoroutineScope,
    onNavigationClickIcon: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.menu_settings), fontSize = 18.sp)
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
                } else if (it == R.string.settings_delete_account_text) {
                    deleteDialogState.value = true
                }
            }

            if (deleteDialogState.value) {
                ShowDeleteConfirmDialog(
                    onDeleteCurrentAccount = viewModel::onDeleteCurrentAccount,
                    state = deleteDialogState
                )
            }
        }
    }
}

@Composable
@Preview
fun ShowDeleteConfirmDialog(
    onDeleteCurrentAccount: (String) -> Unit = {},
    state: MutableState<Boolean> = mutableStateOf(true)
) {
    var passwordValue by remember { mutableStateOf("") }
    if (state.value) {
        AlertDialog(
            onDismissRequest = {
                state.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.settings_delete_account_text))
            },
            text = {
                Column {
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = stringResource(id = R.string.settings_delete_account_description)
                    )
                    PasswordTextField(password = passwordValue, onPasswordChanged = {
                        passwordValue = it
                    })
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onDeleteCurrentAccount.invoke(passwordValue)
                    },
                ) {
                    Text(stringResource(id = R.string.YES))
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        state.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.NO))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AutomaticallyLockModalBottomSheet(
    automaticallyLockAppSheetState: SheetState = rememberModalBottomSheetState(),
    automaticallyLockSelectedItem: StateFlow<AutoLockItemsEnum> = MutableStateFlow(AutoLockItemsEnum.OneMinute),
    scope: CoroutineScope = rememberCoroutineScope(),
    onItemClicked: (AutoLockItemsEnum) -> Unit = {},
) {
    val lastSelectedId = automaticallyLockSelectedItem.collectAsState().value
    ModalBottomSheet(
        sheetState = automaticallyLockAppSheetState,
        content = {
            AUTO_LOCK_CRYPT_ITEMS.forEach {
                ListItem(
                    modifier = Modifier.selectable(selected = false, onClick = {
                        onItemClicked(it.first)
                        scope.launch {
                            automaticallyLockAppSheetState.hide()
                        }
                    }),
                    headlineContent = {},
                    trailingContent = {
                        if (lastSelectedId == it.first) {
                            Text(
                                stringResource(id = it.second),
                                color =  MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(stringResource(id = it.second))
                        }
                    }
                )
            }
        },
        onDismissRequest = {}
    )
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


@Composable
@Preview
fun ListItemsPreview() {
    KryptTheme {
        SettingsItems()
    }
}