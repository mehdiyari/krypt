package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun HomeComposeScreen(
    viewModel: HomeViewModel = viewModel(),
    onSelectAddItemMenuItem: (Int) -> Unit = { },
    onSelectMainMenuItem: (Int) -> Unit = { },
    clickOnLockItem: () -> Unit = { },
    clickOnCards: (FileTypeEnum) -> Unit = { }
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
            val homeCardsModelList = viewModel.filesCounts.collectAsState().value
            LazyColumn(
                contentPadding = PaddingValues(bottom = 85.dp),
            ) {
                items(homeCardsModelList) { homeCardsModel ->
                    HomeItemCard(homeCardsModel, clickOnCards)
                }
            }

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

@Composable
private fun HomeItemCard(
    homeCardsModel: HomeCardsModel,
    clickOnCards: (FileTypeEnum) -> Unit = { }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 4.dp)
            .height(90.dp)
            .selectable(selected = false, onClick = { clickOnCards(getFileTypeEnumBasedOnStringRes(homeCardsModel.name)) }),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Image(
                    painter = painterResource(id = homeCardsModel.icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(55.dp)
                        .padding(10.dp, 0.dp, 0.dp, 0.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )

                Column(
                    modifier = Modifier
                        .padding(3.dp, 9.dp, 4.dp, 0.dp),
                ) {
                    Text(text = stringResource(id = homeCardsModel.name))
                    Text(
                        text = if (homeCardsModel.counts == 0L) stringResource(id = R.string.no_encrypted_file_found) else "${homeCardsModel.counts} ${stringResource(
                            id = R.string.encrypted_file_found
                        )}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

            }
        }
    }
}

private fun getFileTypeEnumBasedOnStringRes(name: Int): FileTypeEnum = when(name) {
    R.string.photos_library -> FileTypeEnum.Photo
    R.string.videos_library -> FileTypeEnum.Video
    R.string.audios_library -> FileTypeEnum.Audio
    R.string.musics_library -> FileTypeEnum.Music
    R.string.texts_library -> FileTypeEnum.Text
    else -> throw IllegalArgumentException()
}
