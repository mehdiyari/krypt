package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.home.ADD_ITEMS
import ir.mehdiyari.krypt.ui.home.MAIN_MENU_ITEMS
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KryptBottomAppBar(
    addItemsBottomSheetState: SheetState,
    mainMenuBottomSheetState: SheetState,
    coroutineScope: CoroutineScope,
    onLockClicked: () -> Unit
) {
    BottomAppBar(
        modifier = Modifier.clip(
            MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
        ),
    ) {
        IconButton(modifier = Modifier
            .weight(1f)
            .wrapContentWidth(Alignment.Start), onClick = {
            coroutineScope.launch {
                addItemsBottomSheetState.hide()
                mainMenuBottomSheetState.show()
            }
        }) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = stringResource(id = R.string.krypt_main_menu)
            )
        }
        IconButton(onClick = onLockClicked) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = stringResource(id = R.string.lock_app_content_description)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFab(
    addItemsBottomSheetState: SheetState,
    mainMenuBottomSheetState: SheetState,
    coroutineScope: CoroutineScope
) {
    FloatingActionButton(onClick = {
        coroutineScope.launch {
            if (addItemsBottomSheetState.isVisible) {
                addItemsBottomSheetState.hide()
            } else {
                mainMenuBottomSheetState.hide()
                addItemsBottomSheetState.show()
            }
        }
    }) {
        Icon(
            Icons.Filled.Add,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = stringResource(id = R.string.add_items)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuBottomSheet(
    mainMenuBottomSheetState: SheetState,
    onSelectMainMenuItem: (Int) -> Unit,
    scope: CoroutineScope
) {
    ModalBottomSheet(
        sheetState = mainMenuBottomSheetState,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = stringResource(id = R.string.app_menu_subtitle),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            MAIN_MENU_ITEMS.forEach {
                ListItem(
                    modifier = Modifier.selectable(selected = false, onClick = {
                        onSelectMainMenuItem(it.second)
                        scope.launch {
                            mainMenuBottomSheetState.hide()
                        }
                    }),
                    headlineContent = {},
                    trailingContent = { Text(stringResource(id = it.second)) },
                    leadingContent = {
                        Icon(
                            painterResource(id = it.first),
                            contentDescription = stringResource(id = it.second)
                        )
                    }
                )
            }
        },
        onDismissRequest = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(
    addItemsBottomSheetState: SheetState,
    onSelectAddItemMenuItem: (Int) -> Unit,
    scope: CoroutineScope
) {
    ModalBottomSheet(
        sheetState = addItemsBottomSheetState,
        content = {
            ADD_ITEMS.forEach {
                ListItem(
                    modifier = Modifier.selectable(selected = false, onClick = {
                        onSelectAddItemMenuItem(it.second)
                        scope.launch {
                            addItemsBottomSheetState.hide()
                        }
                    }),
                    headlineContent = {},
                    trailingContent = {
                        if (it.second != -1)
                            Text(stringResource(id = it.second))
                    },
                    leadingContent = {
                        if (it.first != -1)
                            Icon(
                                painterResource(id = it.first),
                                contentDescription = stringResource(id = it.second)
                            )
                    }
                )
            }
        },
        onDismissRequest = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BottomAppBarPreview() {
    KryptTheme {
        KryptBottomAppBar(
            addItemsBottomSheetState = rememberModalBottomSheetState(),
            mainMenuBottomSheetState = rememberModalBottomSheetState(),
            coroutineScope = rememberCoroutineScope()
        ) {

        }
    }
}