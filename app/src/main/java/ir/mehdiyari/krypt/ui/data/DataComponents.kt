package ir.mehdiyari.krypt.ui.data

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataScreenScaffold(
    modifier: Modifier,
    onNavigationClicked: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.menu_data_usage),
                        textAlign = TextAlign.Start
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigationClicked()
                        },
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack, "",
                            modifier = modifier.padding(start = 4.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Composable
fun FileSizeView(modifier: Modifier, fileSizeState: State<String>) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.krypt_file_sizes),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = fileSizeState.value,
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            modifier = modifier.padding(top = 8.dp)
        )

        DataBaseDivider(modifier)
    }
}

@Composable
fun BackupView(
    modifier: Modifier,
    lastBackupState: State<String?>,
    backupState: State<BackupViewState?>,
    backupNowClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        ConstraintLayout(
            modifier = modifier.fillMaxWidth()
        ) {
            val (title, dateTime) = createRefs()

            Text(
                text = stringResource(id = R.string.last_backup_date),
                fontWeight = FontWeight.Bold,
                modifier = modifier.constrainAs(title) {
                    start.linkTo(parent.start, margin = 20.dp)
                }
            )

            val dateTimeString = if (lastBackupState.value.isNullOrBlank()) {
                stringResource(id = R.string.no_backup_yet)
            } else {
                lastBackupState.value ?: ""
            }

            Text(
                text = dateTimeString,
                fontWeight = FontWeight.Bold,
                modifier = modifier.constrainAs(dateTime) {
                    end.linkTo(parent.end, margin = 20.dp)
                }
            )
        }

        val stateValue = backupState.value
        if (stateValue is BackupViewState.Started) {
            CircularProgressIndicator(
                modifier = modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
            )
        } else {
            val colorIfError = if (stateValue is BackupViewState.Failed) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }

            Button(
                modifier = modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorIfError
                ),
                onClick = {
                    backupNowClick()
                }
            ) {
                when (stateValue) {
                    is BackupViewState.Failed -> {
                        Text(text = stringResource(id = R.string.retry_backup))
                    }

                    else -> {
                        Text(text = stringResource(id = R.string.backup))
                    }
                }
            }
        }

        DataBaseDivider(modifier)
    }
}

@Composable
fun DataBaseDivider(modifier: Modifier) {
    Divider(
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun DeleteBackupFileDialog(
    modifier: Modifier,
    deleteDialogState: MutableState<Pair<Boolean, Int>> = mutableStateOf(true to -1),
    onDeleteBackUp: (Int) -> Unit,
) {
    if (deleteDialogState.value.first) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                deleteDialogState.value = false to -1
            },
            title = {
                Text(text = stringResource(id = R.string.delete_backup_file_title))
            },
            text = {
                Text(
                    modifier = modifier.padding(bottom = 10.dp),
                    text = stringResource(id = R.string.delete_backup_file)
                )
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onDeleteBackUp(deleteDialogState.value.second)
                        deleteDialogState.value = false to -1
                    },
                ) {
                    Text(stringResource(id = R.string.YES))
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        deleteDialogState.value = false to -1
                    }
                ) {
                    Text(stringResource(id = R.string.NO))
                }
            }
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun FileSizePreview() {
    KryptTheme {
        FileSizeView(modifier = Modifier, fileSizeState = mutableStateOf("500 MB"))
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun DeleteBackupFileDialogPreview() {
    KryptTheme {
        DeleteBackupFileDialog(
            modifier = Modifier,
            deleteDialogState = mutableStateOf(true to R.string.delete_backup_file),
            onDeleteBackUp = {},
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun BackupViewPreview() {
    KryptTheme {
        BackupView(
            modifier = Modifier,
            lastBackupState = mutableStateOf("2022 April 22 - 22:30"),
            backupState = mutableStateOf(null),
            backupNowClick = {},
        )
    }
}

@Composable
@Preview
fun DataScreenScaffoldPreview() {
    KryptTheme {
        DataScreenScaffold(
            modifier = Modifier,
            onNavigationClicked = {},
        ) {

        }
    }
}