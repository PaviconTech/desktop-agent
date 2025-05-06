package com.pavicontech.desktop.agent.presentation.screens.signIn.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.presentation.screens.signIn.SignInScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToPasswordReset: () -> Unit,
    onNavigateToDashboard: () -> Unit,
) {



    val viewModel: SignInScreenViewModel = koinViewModel()


    LaunchedEffect(viewModel.signInState){
        if (viewModel.signInState.isSuccessful){
            onNavigateToDashboard()
        }
    }

    Surface(
        color = MaterialTheme.colors.primary
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        if (
                            viewModel.kraPin.isNotBlank() &&
                            viewModel.username.isNotBlank() &&
                            viewModel.password.isNotBlank()
                        ) {
                            viewModel.signIn()
                            true
                        } else false
                    } else false
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Etims",
                        style = MaterialTheme.typography.h1,
                        color = Color.White
                    )
                    Text(
                        text = "Sync",
                        style = MaterialTheme.typography.h1,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
                Card(
                    elevation = 8.dp,

                    ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(0.4f)
                            .padding(vertical = 16.dp, horizontal = 16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.h1,
                                color = MaterialTheme.colors.primary
                            )
                        }
                        CredentialInput(
                            value = viewModel.kraPin,
                            onValueChange = viewModel::onKraPinChange,
                            placeHolder = "Kra Pin"
                        )
                        CredentialInput(
                            value = viewModel.username,
                            onValueChange = viewModel::onUsernameChange,
                            placeHolder = "Username"
                        )
                        CredentialInput(
                            value = viewModel.password,
                            onValueChange = viewModel::onPasswordChange,
                            placeHolder = "Password"
                        )

                        AnimatedVisibility(!viewModel.signInState.isLoading) {
                            Button(
                                onClick = {
                                    viewModel.signIn()
                                },
                                enabled = viewModel.kraPin.isNotBlank()
                                        && viewModel.username.isNotBlank()
                                        && viewModel.password.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                                    .padding(top = 16.dp)

                            ) {
                                Text("Sign In")
                            }
                        }

                        AnimatedVisibility(viewModel.signInState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 16.dp, bottom = 16.dp)
                        ) {
                            Text(
                                text = "Forgot password? ",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onBackground
                            )

                            TextButton(
                                onClick = onNavigateToPasswordReset,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colors.secondary,
                                    backgroundColor = Color.Transparent
                                )
                            ) {
                                Text(
                                    text = "reset",
                                )
                            }
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}