package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun HomeComposeScreen(
    onSelectAddItemMenuItem: (Int) -> Unit = { },
    onSelectMainMenuItem: (Int) -> Unit = { },
    clickOnLockItem: () -> Unit = { }
) {
    KryptTheme {
        val addItemsBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val mainMenuBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()
        Scaffold(
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
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
                        tint = MaterialTheme.colors.onSurface,
                        contentDescription = stringResource(id = R.string.add_items)
                    )
                }
            },
            isFloatingActionButtonDocked = true,
            bottomBar = {
                BottomAppBar(
                    cutoutShape = MaterialTheme.shapes.small.copy(
                        CornerSize(percent = 50)
                    )
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        IconButton(onClick = {
                            scope.launch {
                                addItemsBottomSheetState.hide()
                                mainMenuBottomSheetState.show()
                            }
                        }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = stringResource(id = R.string.krypt_main_menu)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f, true))
                    IconButton(onClick = { clickOnLockItem() }) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = stringResource(id = R.string.lock_app_content_description)
                        )
                    }
                }
            }
        ) {
            // Screen content
            ModalBottomSheetLayout(
                sheetState = addItemsBottomSheetState,
                sheetContent = {
                    ADD_ITEMS.forEach {
                        ListItem(
                            modifier = Modifier.selectable(selected = false, onClick = {
                                onSelectAddItemMenuItem(it.second)
                                scope.launch {
                                    addItemsBottomSheetState.hide()
                                }
                            }),
                            text = { Text(stringResource(id = it.second)) },
                            icon = {
                                Icon(
                                    painterResource(id = it.first),
                                    contentDescription = stringResource(id = it.second)
                                )
                            }
                        )
                    }
                }
            ) { }
        }

        ModalBottomSheetLayout(
            sheetState = mainMenuBottomSheetState,
            sheetContent = {
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
                        text = { Text(stringResource(id = it.second)) },
                        icon = {
                            Icon(
                                painterResource(id = it.first),
                                contentDescription = stringResource(id = it.second)
                            )
                        }
                    )
                }
            }
        ) { }
    }
}