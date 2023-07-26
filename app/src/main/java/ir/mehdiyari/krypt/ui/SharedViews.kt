package ir.mehdiyari.krypt.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme


@Composable
fun ManageExternalPermissionDialog(
    modifier: Modifier,
    state: MutableState<Boolean>,
    onGrantPermissionClicked: () -> Unit,
) {
    if (state.value) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                state.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.manager_external_permission))
            },
            text = {
                Column {
                    Text(
                        modifier = modifier.padding(bottom = 10.dp),
                        text = stringResource(id = R.string.manager_external_permission_description)
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onGrantPermissionClicked.invoke()
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
            })
    }

}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun ManageExternalPermissionDialogPreview() {
    KryptTheme {
        ManageExternalPermissionDialog(modifier = Modifier, state = mutableStateOf(true)) {

        }
    }
}