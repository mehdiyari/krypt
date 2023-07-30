package ir.mehdiyari.krypt.backup

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.shared.designsystem.resources.R as DesignSystemR

@Composable
internal fun BackupList(
    modifier: Modifier,
    backupList: State<List<BackupViewData>>,
    onSaveClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    if (backupList.value.isNotEmpty()) {
        Column {
            Text(
                text = stringResource(id = R.string.all_backups),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp)
            )

            LazyRow(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp)) {
                items(backupList.value) { backupItem ->
                    BackupItem(modifier, backupItem, onSaveClick, onDeleteClick)
                }
            }

            DataBaseDivider(modifier)
        }
    }

}

@Composable
internal fun BackupItem(
    modifier: Modifier,
    backupViewData: BackupViewData,
    onSaveClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    Card(
        modifier = modifier
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
                modifier = modifier.padding(6.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )

            val dateTimeString = backupViewData.dateTime.ifBlank {
                stringResource(id = R.string.no_backup_yet)
            }

            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp),
                text = dateTimeString,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )

            Row(modifier = modifier.padding(bottom = 8.dp, top = 16.dp)) {

                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_save_as),
                    "",
                    modifier = modifier
                        .selectable(false, onClick = {
                            onSaveClick(backupViewData.id)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 6.dp, end = 6.dp)
                        .size(18.dp)
                )

                Icon(
                    Icons.Filled.Delete, "", modifier = modifier
                        .selectable(false, onClick = {
                            onDeleteClick(backupViewData.id)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 6.dp, end = 6.dp)
                        .size(18.dp)
                )
            }

            Spacer(modifier = modifier.size(2.dp))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
private fun BackupItemPreview(
    @PreviewParameter(
        BackupsPreviewParameterProvider::class,
        limit = 1
    ) backupList: List<BackupViewData>
) {
    KryptTheme {
        BackupItem(
            modifier = Modifier,
            backupViewData = backupList[0],
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
internal fun BackupListPreview(
    @PreviewParameter(
        BackupsPreviewParameterProvider::class,
        limit = 5
    ) backupList: List<BackupViewData>
) {
    KryptTheme {
        BackupList(
            modifier = Modifier,
            backupList = mutableStateOf(backupList),
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}

internal class BackupsPreviewParameterProvider : PreviewParameterProvider<List<BackupViewData>> {
    override val values: Sequence<List<BackupViewData>> = sequenceOf(
        mutableListOf<BackupViewData>().apply {
            add(BackupViewData(1, "13/12/2014 13:20", "300 MB"))
            add(BackupViewData(2, "23/12/2014 18:20", "254 MB"))
            add(BackupViewData(3, "01/12/2014 13:25", "3 GB"))
        }
    )
}