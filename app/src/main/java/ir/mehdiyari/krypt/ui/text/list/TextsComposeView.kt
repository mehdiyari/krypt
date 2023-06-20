package ir.mehdiyari.krypt.ui.text.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme


@Composable
fun NewNoteFab(
    newNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = {
            newNoteClick()
        },
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_pen),
                contentDescription = stringResource(id = R.string.add_new_text)
            )
        },
        text = { Text(text = stringResource(id = R.string.add_new_text)) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun TextCard(
    text: TextEntity,
    onTextClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .selectable(
                selected = false,
                onClick = { onTextClick.invoke(text.id) }),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pen),
                modifier = Modifier
                    .size(32.dp),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
            ) {
                Text(
                    text = text.title,
                    maxLines = 1
                )
                Text(
                    text = text.content,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun TextCardPreview() {
    KryptTheme {
        Surface {
            TextCard(
                text = TextEntity(id = 10001L, title = "Title", content = "Text"),
                onTextClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
