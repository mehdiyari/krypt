package ir.mehdiyari.krypt.restore.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import ir.mehdiyari.krypt.files.logic.utils.getRealPathBasedOnUri

@Composable
internal fun OpenBackupFile(
    onFileSelected: (String?) -> Unit,
) {
    val context = LocalContext.current
    val openKrpFileLauncher = rememberLauncherForActivityResult(contract = object :
        ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }, onResult = {
        onFileSelected(if (it != null) context.getRealPathBasedOnUri(it) else null)
    })

    SideEffect {
        openKrpFileLauncher.launch(Unit)
    }
}