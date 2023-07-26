package ir.mehdiyari.krypt.setting.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.setting.R
import ir.mehdiyari.krypt.shared.designsystem.components.PasswordTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun SettingsScreenContent(
    modifier: Modifier,
    viewModel: SettingsViewModel,
    deleteDialogState: MutableState<Boolean>,
    topPadding: Dp,
    openAutoLockBottomSheet: () -> Unit
) {
    Row(modifier = modifier.padding(top = topPadding)) {
        SettingItems(modifier = modifier) {
            if (it == R.string.settings_lock_auto) {
                openAutoLockBottomSheet()
            } else if (it == R.string.settings_delete_account_text) {
                deleteDialogState.value = true
            }
        }

        if (deleteDialogState.value) {
            ShowDeleteConfirmDialog(
                modifier = modifier,
                onDeleteCurrentAccount = viewModel::onDeleteCurrentAccount,
                state = deleteDialogState
            )
        }
    }
}


@Composable
internal fun ShowDeleteConfirmDialog(
    modifier: Modifier,
    onDeleteCurrentAccount: (String) -> Unit,
    state: MutableState<Boolean>,
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
                        modifier = modifier.padding(bottom = 10.dp),
                        text = stringResource(id = R.string.settings_delete_account_description)
                    )
                    PasswordTextField(
                        password = passwordValue,
                        hint = stringResource(id = ir.mehdiyari.krypt.shared.designsystem.resources.R.string.account_password),
                        onPasswordChanged = {
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
                    Text(stringResource(id = ir.mehdiyari.krypt.shared.designsystem.resources.R.string.YES))
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        state.value = false
                    }
                ) {
                    Text(stringResource(id = ir.mehdiyari.krypt.shared.designsystem.resources.R.string.NO))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AutomaticallyLockModalBottomSheet(
    modifier: Modifier,
    automaticallyLockSelectedItem: StateFlow<AutoLockItemsEnum>,
    scope: CoroutineScope,
    onItemClicked: (AutoLockItemsEnum) -> Unit,
    dismiss: () -> Unit
) {
    val lastSelectedId = automaticallyLockSelectedItem.collectAsStateWithLifecycle().value
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        content = {
            AUTO_LOCK_CRYPT_ITEMS.forEach {
                ListItem(
                    modifier = modifier.selectable(selected = false, onClick = {
                        onItemClicked(it.first)
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (sheetState.isVisible.not()){
                                dismiss()
                            }
                        }
                    }),
                    headlineContent = {},
                    trailingContent = {
                        if (lastSelectedId == it.first) {
                            Text(
                                stringResource(id = it.second),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(stringResource(id = it.second))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        },
        onDismissRequest = {dismiss()}
    )
}


@Composable
@Preview
internal fun ListItemsPreview() {
    KryptTheme {
        SettingItems(modifier = Modifier, onItemClick = {})
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
internal fun AutomaticallyLockModalBottomSheetPreview() {
    KryptTheme {
        AutomaticallyLockModalBottomSheet(
            modifier = Modifier,
            automaticallyLockSelectedItem = MutableStateFlow(AutoLockItemsEnum.OneMinute),
            scope = rememberCoroutineScope(),
            {}
        ) {}
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
internal fun ShowDeleteConfirmDialogPreview() {
    KryptTheme {
        ShowDeleteConfirmDialog(
            modifier = Modifier,
            onDeleteCurrentAccount = {},
            state = mutableStateOf(true)
        )
    }
}