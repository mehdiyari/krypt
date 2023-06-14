package ir.mehdiyari.krypt.ui.data

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun DataScreen(
    viewModel: DataViewModel = viewModel(),
    onNavigationClicked: () -> Unit = {}
) {
    KryptTheme {
        DataScreenScaffold(onNavigationClicked = onNavigationClicked) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                val fileSizeState = viewModel.fileSizes.collectAsState()
                FileSizeView(fileSizeState)
                val lastBackupState = viewModel.lastBackupDateTime.collectAsState()
                val backupState = viewModel.backupViewState.collectAsState()
                BackupView(lastBackupState, backupState, viewModel::backupNow)
                val backupList = viewModel.backups.collectAsState()

                val deleteDialogState = remember { mutableStateOf(false to -1) }
                BackupList(backupList, viewModel::onSaveBackup) {
                    deleteDialogState.value = true to it
                }

                DeleteBackupFileDialog(deleteDialogState, viewModel)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataScreenScaffold(
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
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Composable
@Preview
fun FileSizeView(fileSizeState: State<String> = mutableStateOf("500 MB")) {
    Column(
        modifier = Modifier
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
            modifier = Modifier.padding(top = 8.dp)
        )

        DataBaseDivider()
    }
}

@Composable
@Preview
fun BackupView(
    lastBackupState: State<String?> = mutableStateOf("2022 April 22 - 22:30"),
    backupState: State<BackupViewState?> = mutableStateOf(null),
    backupNowClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (title, dateTime) = createRefs()

            Text(
                text = stringResource(id = R.string.last_backup_date),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title) {
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
                modifier = Modifier.constrainAs(dateTime) {
                    end.linkTo(parent.end, margin = 20.dp)
                }
            )
        }

        val stateValue = backupState.value
        if (stateValue is BackupViewState.Started) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
            )
        } else {
            val colorIfError = if (stateValue is BackupViewState.Failed) {
                 MaterialTheme.colorScheme.error
            } else {
                 MaterialTheme.colorScheme.primary
            }

            Button(
                modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
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

        DataBaseDivider()
    }
}

@Composable
@Preview
fun BackupList(
    backupList: State<List<BackupViewData>> = mutableStateOf(
        listOf(
            BackupViewData(1, "", "300 MB"),
            BackupViewData(2, "", "254 MB"),
            BackupViewData(3, "", "3 GB"),
        )
    ),
    onSaveClick: (Int) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {},
) {
    if (backupList.value.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(id = R.string.all_backups),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp)
            )

            LazyRow(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp)) {
                items(backupList.value) { backupItem ->
                    BackupItem(backupItem, onSaveClick, onDeleteClick)
                }
            }

            DataBaseDivider()
        }
    }

}

@Composable
@Preview
fun BackupItem(
    backupViewData: BackupViewData =
        BackupViewData(1, "", "300 MB"),
    onSaveClick: (Int) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(id = R.string.backup_card_title)} #${backupViewData.id}",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Image(
                painter = painterResource(R.drawable.ic_backup_file),
                contentDescription = "",
                modifier = Modifier.padding(6.dp),
                colorFilter = ColorFilter.tint( MaterialTheme.colorScheme.onSurface)
            )

            val dateTimeString = backupViewData.dateTime.ifBlank {
                stringResource(id = R.string.no_backup_yet)
            }

            Text(
                text = dateTimeString,
                fontSize = 14.sp
            )

            Row(modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)) {

                Icon(
                    painter = painterResource(R.drawable.ic_save_as), "", modifier = Modifier
                        .selectable(false, onClick = {
                            onSaveClick(backupViewData.id)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 6.dp, end = 6.dp)
                        .size(18.dp)
                )

                Icon(
                    Icons.Filled.Delete, "", modifier = Modifier
                        .selectable(false, onClick = {
                            onDeleteClick(backupViewData.id)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.size(2.dp))
        }
    }
}

@Composable
private fun DataBaseDivider() {
    Divider(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
@Preview
private fun DeleteBackupFileDialog(
    deleteDialogState: MutableState<Pair<Boolean, Int>> = mutableStateOf(true to -1),
    viewModel: DataViewModel? = null
) {
    if (deleteDialogState.value.first) {
        AlertDialog(
            onDismissRequest = {
                deleteDialogState.value = false to -1
            },
            title = {
                Text(text = stringResource(id = R.string.delete_backup_file_title))
            },
            text = {
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = stringResource(id = R.string.delete_backup_file)
                )
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        viewModel?.onDeleteBackup(deleteDialogState.value.second)
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


@Composable
@Preview
fun TestAllDataViewsTogether() {
    KryptTheme {
        DataScreenScaffold {
            Column {
                FileSizeView()
                BackupView()
                BackupList()
            }
        }
    }
}