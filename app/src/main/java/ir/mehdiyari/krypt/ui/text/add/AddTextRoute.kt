package ir.mehdiyari.krypt.ui.text.add

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R

@Composable
fun AddTextRoute(
    modifier: Modifier = Modifier, viewModel: AddTextViewModel = hiltViewModel()
) {

    val sharedText = viewModel.addTextArgs.sharedText
    val argsState by viewModel.argsTextViewState.collectAsStateWithLifecycle()

    val isEditMode = argsState is AddTextArgsViewState.TextArg

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    if (isEditMode && title.isEmpty() && content.isEmpty()) {
        title = (argsState as AddTextArgsViewState.TextArg).textEntity.title
        content = (argsState as AddTextArgsViewState.TextArg).textEntity.content
    } else {
        if (sharedText.isNotBlank()) {
            content = sharedText
        }
    }

    val context = LocalContext.current
    LaunchedEffect(argsState) {
        (argsState as? AddTextArgsViewState.Error)?.let {
            Toast.makeText(context, it.errorResId, Toast.LENGTH_LONG)
                .show()
            //FIXME MHD: close screen
        }
    }

    val saveNoteState by viewModel.saveNoteState.collectAsStateWithLifecycle()
    LaunchedEffect(saveNoteState) {
        if (saveNoteState == true) {
            Toast.makeText(context, R.string.successfully_encrypt_note, Toast.LENGTH_LONG)
                .show()
            //FIXME MHD: close screen
        } else if (saveNoteState == false) {
            Toast.makeText(context, R.string.failed_to_encrypt_note, Toast.LENGTH_LONG)
                .show()
        }
    }

    val deleteNoteState by viewModel.deleteNoteState.collectAsStateWithLifecycle()
    LaunchedEffect(deleteNoteState) {
        if (deleteNoteState == true) {
            //FIXME MHD: close screen
            Toast.makeText(
                context,
                R.string.delete_text_was_successfully,
                Toast.LENGTH_LONG
            ).show()
        } else if (deleteNoteState == false) {
            Toast.makeText(
                context,
                R.string.delete_text_was_unsuccessfully,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val saveNoteValidation by viewModel.saveNoteValidation.collectAsStateWithLifecycle()
    LaunchedEffect(saveNoteValidation) {
        if (saveNoteValidation != null) {
            Toast.makeText(context, saveNoteValidation!!, Toast.LENGTH_SHORT).show()
        }
    }

    AddTextScreen(
        title = title,
        onTitleChanged = { title = it },
        content = content,
        onContentChanged = { content = it },
        isEditMode = isEditMode,
        saveNote = {
            viewModel.saveNote(title, content)
        },
        deleteNote = {
            viewModel.deleteNote()
        },
        modifier = modifier
    )


}

@Composable
fun AddTextScreen(
    title: String,
    onTitleChanged: (String) -> Unit,
    content: String,
    onContentChanged: (String) -> Unit,
    isEditMode: Boolean,
    saveNote: () -> Unit,
    deleteNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column {
            TopBar(text = title, onTextChanged = onTitleChanged, onNavigationClickIcon = {

            }, modifier = Modifier.fillMaxWidth())
            ContentTextField(
                text = content,
                onTextChanged = onContentChanged,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            )
        }

        if (isEditMode) {
            EditAndDeleteButtons(
                saveNote = saveNote,
                deleteNote = deleteNote,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        } else {
            SaveTextFab(onSaveClick = saveNote, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}