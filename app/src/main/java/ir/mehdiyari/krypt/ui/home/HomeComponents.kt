package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.utils.KryptTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeItemCard(
    homeCardsModel: HomeCardsModel,
    onCardClicked: (FileTypeEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
        onClick = {
            onCardClicked(getFileTypeEnumBasedOnStringRes(homeCardsModel.name))
        }) {
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
                    modifier = Modifier.padding(3.dp, 9.dp, 4.dp, 0.dp),
                ) {
                    Text(text = stringResource(id = homeCardsModel.name))
                    Text(
                        text = if (homeCardsModel.counts == 0L) stringResource(id = R.string.no_encrypted_file_found) else "${homeCardsModel.counts} ${
                            stringResource(
                                id = R.string.encrypted_file_found
                            )
                        }", fontSize = 12.sp, color = Color.Gray
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun HomeItemCardPreview(@PreviewParameter(HomeCardsPreviewParameterProvider::class) cards: List<HomeCardsModel>) {

    KryptTheme {
        Surface {
            HomeItemCard(
                homeCardsModel = cards[0],
                onCardClicked = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }

}

class HomeCardsPreviewParameterProvider : PreviewParameterProvider<List<HomeCardsModel>> {
    override val values: Sequence<List<HomeCardsModel>>
        get() = sequenceOf(
            listOf(
                HomeCardsModel(
                    R.drawable.ic_gallery_50, R.string.medias_library, 10
                ), HomeCardsModel(
                    R.drawable.ic_gallery_50, R.string.medias_library, 10
                ), HomeCardsModel(
                    R.drawable.ic_gallery_50, R.string.medias_library, 10
                )
            )
        )
}

private fun getFileTypeEnumBasedOnStringRes(name: Int): FileTypeEnum = when (name) {
    R.string.medias_library -> FileTypeEnum.Photo
    R.string.audios_library -> FileTypeEnum.Audio
    R.string.texts_library -> FileTypeEnum.Text
    else -> throw IllegalArgumentException()
}