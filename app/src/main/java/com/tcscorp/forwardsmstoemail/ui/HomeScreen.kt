package com.tcscorp.forwardsmstoemail.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcscorp.forwardsmstoemail.R
import com.tcscorp.forwardsmstoemail.widgets.ProgressDialog

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val messagesUiState = viewModel.messages.collectAsState().value
    val applyPreferencesUiState = viewModel.applyPreferences.collectAsState().value
    val preferencesUiState = viewModel.preferences.collectAsState().value
    var forwardedMessagesCount by remember { mutableStateOf(0) }
    var pendingMessagesCount by remember { mutableStateOf(0) }

    preferencesUiState.run {
        doOnProgress {
            ProgressDialog()
            Log.i("SMSForwarderLogs", "Reading preferences...")
        }
        doOnSuccess {
            Log.i("SMSForwarderLogs", "Got preferences -> $it")
        }
        doOnError {
            Log.i("SMSForwarderLogs", "Failed while getting preferences -> ${it?.message}")
        }
    }

    Scaffold(
        topBar = { AppBar() }
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                Text(
                    text = "System Status",
                    style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily(Font(R.font.productsansregular)))
                )
                messagesUiState.run {
                    doOnProgress { ProgressDialog() }
                    doOnSuccess { messages ->
                        forwardedMessagesCount =
                            messages.filter { message -> message.forwarded }.size
                        pendingMessagesCount = messages.size - forwardedMessagesCount
                    }
                    doOnError { }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SystemStatus(Modifier.fillMaxWidth(), forwardedMessagesCount, pendingMessagesCount)
                Spacer(modifier = Modifier.height(52.dp))
                Text(
                    text = "System Settings",
                    style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily(Font(R.font.productsansregular)))
                )
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSection(Modifier.fillMaxWidth(), applyPreferencesUiState, viewModel)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.applySettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 1.dp,
                        pressedElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(16),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.apply_settings),
                        style = MaterialTheme.typography.subtitle2.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.productsansregular))
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun SystemStatus(
    modifier: Modifier = Modifier,
    forwardedCount: Int,
    pendingCount: Int
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color(0xFFe3f2fd)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Forwarded SMS", style = MaterialTheme.typography.body1.copy(
                        fontFamily = FontFamily(
                            Font(R.font.productsansregular)
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = forwardedCount.toString(), style = MaterialTheme.typography.h6.copy(
                        fontFamily = FontFamily(
                            Font(R.font.productsansregular)
                        ),
                        fontWeight = FontWeight.Black
                    )
                )
            }
            Divider(
                color = Color(0xFF9fa8da), modifier = Modifier
                    .width(1.dp)
                    .height(56.dp)
            )
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pending SMS", style = MaterialTheme.typography.body1.copy(
                        fontFamily = FontFamily(
                            Font(R.font.productsansregular)
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pendingCount.toString(), style = MaterialTheme.typography.h6.copy(
                        fontFamily = FontFamily(
                            Font(R.font.productsansregular)
                        ),
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    modifier: Modifier,
    applyPreferencesUiState: ApplyPreferencesUiState,
    viewModel: HomeViewModel
) {
    val emailAddress: String by viewModel.emailAddress.collectAsState()
    val emailPassword: String by viewModel.emailPassword.collectAsState()
    val phoneNumber: String by viewModel.phoneNumber.collectAsState()
    val mailServer: String by viewModel.mailServer.collectAsState()
    val mailHost: String by viewModel.mailHost.collectAsState()
    val mailPort: String by viewModel.mailPort.collectAsState()
    val context = LocalContext.current
    applyPreferencesUiState.settingsError?.let {
        Log.i("SMSForwarderLogs", "Got validation errors")
    }

    applyPreferencesUiState.run {
        doOnProgress {
            ProgressDialog()

        }
        doOnError {
            Toast.makeText(
                context,
                "An error occurred while applying settings -> ${it?.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    Column(modifier) {
        MaterialTextInput(
            keyboardType = KeyboardType.Phone,
            label = "Phone Number",
            value = phoneNumber
        ) {
            viewModel.onPhoneNumberChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Email,
            label = "Email Address",
            value = emailAddress
        ) {
            viewModel.onEmailAddressChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Password,
            label = "Email Password",
            value = emailPassword,
            isPassword = true
        ) {
            viewModel.onEmailPasswordChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Text,
            label = "Mail Server",
            value = mailServer
        ) {
            viewModel.onMailServerChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Text,
            label = "Mail Host",
            value = mailHost
        ) {
            viewModel.onMailHostChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Number,
            label = "Mail Port",
            value = mailPort
        ) {
            viewModel.onMailPortChange(it)
        }
    }
}

@Composable
fun MaterialTextInput(
    keyboardType: KeyboardType,
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            maxLines = 1,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AppBar() {
    TopAppBar(contentPadding = PaddingValues(start = 16.dp), contentColor = Color.White) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily(Font(R.font.productsansregular)))
        )
    }
}