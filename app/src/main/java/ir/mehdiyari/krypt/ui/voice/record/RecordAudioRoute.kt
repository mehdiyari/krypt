package ir.mehdiyari.krypt.ui.voice.record

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecordAudioRoute(
    modifier: Modifier,
    viewModel: RecordVoiceViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(id = R.string.record_audio))
        }, navigationIcon = {
            IconButton(onClick = {
                onBackPressed()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        })
    }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) {
        AddAudioScreenContent(
            modifier = modifier,
            onBackPressed = onBackPressed,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            recordVoiceViewState = viewModel.recordVoiceViewState,
            onSaveRecordRetry = viewModel::saveRecordRetry,
            startRecord = viewModel::startRecord,
            recordTimerState = viewModel.recordTimer,
            actionButtonState = viewModel.actionsButtonState,
        )
    }
}