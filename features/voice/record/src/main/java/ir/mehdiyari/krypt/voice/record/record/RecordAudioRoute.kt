package ir.mehdiyari.krypt.voice.record.record

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ir.mehdiyari.krypt.voice.record.R
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun RecordAudioRoute(
    modifier: Modifier,
    viewModel: RecordVoiceViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.startRecord()
        } else {
            Toast.makeText(context, R.string.microphone_permission_error, Toast.LENGTH_SHORT).show()
        }
    }
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
            recordTimerState = viewModel.recordTimer,
            actionButtonState = viewModel.actionsButtonState,
            startRecord = {
                val recordPermission = Manifest.permission.RECORD_AUDIO
                if (context.checkSelfPermission(recordPermission) == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startRecord()
                } else {
                    micPermissionLauncher.launch(recordPermission)
                }
            }
        )
    }
}