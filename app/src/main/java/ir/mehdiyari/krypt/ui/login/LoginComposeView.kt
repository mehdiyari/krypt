package ir.mehdiyari.krypt.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.PasswordTextField
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun LoginComposeScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onClick: (username: String, password: String) -> Unit = { _, _ -> },
    onCreateAccountClick: () -> Unit = {}
) {
    KryptTheme {
        val passwordValue = remember { mutableStateOf(TextFieldValue()) }
        val expanded = remember { mutableStateOf(false) }
        val accountNameState = remember { mutableStateOf(TextFieldValue()) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 65.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.krypt),
                contentDescription = stringResource(id = R.string.splash_content_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp, 20.dp, 25.dp, 3.dp)
            ) {
                Column {
                    TextField(
                        value = accountNameState.value,
                        onValueChange = { accountNameState.value = it },
                        label = { Text(text = stringResource(id = R.string.account_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = MaterialTheme.colors.onBackground)
                    )

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                    ) {
                        loginViewModel.allAccountsNameState.collectAsState(
                            initial = listOf(),
                        ).value.forEach { accountName ->
                            DropdownMenuItem(
                                onClick = {
                                    expanded.value = false
                                    accountNameState.value = TextFieldValue(accountName)
                                }
                            ) {
                                Text(
                                    accountName, modifier = Modifier
                                        .wrapContentWidth(),
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .padding(10.dp)
                        .clickable(
                            onClick = { expanded.value = true }
                        )
                )
            }

            PasswordTextField(passwordValue)
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(20.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { onClick(accountNameState.value.text, passwordValue.value.text) },
                icon = {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.button_login)
                    )
                },
                text = { Text(text = stringResource(id = R.string.button_login)) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(20.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { onCreateAccountClick() },
                text = { Text(text = stringResource(id = R.string.button_create_new_account)) },
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            )
        }
    }
}