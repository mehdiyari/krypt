package ir.mehdiyari.krypt.ui.voice.audios

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Preview
fun AudiosScreen(
    navController: NavController? = null
) {
    KryptTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.audios_library))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController?.navigateUp()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                )
            }
        ) {

        }
    }
}

@Composable
@Preview
fun AudioItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_audio_play),
                    contentDescription = ""
                )
            }

            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (columnRef, dateText) = createRefs()
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .constrainAs(columnRef) {}
                ) {
                    Text(text = "Voice #60", fontWeight = FontWeight.Bold)
                    Text(text = "01:10:23", fontSize = 12.sp)
                }

                Text(text = "2023/01/01 23:32", fontSize = 10.sp, modifier = Modifier
                    .padding(end = 4.dp)
                    .constrainAs(dateText) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    })
            }

        }
    }
}