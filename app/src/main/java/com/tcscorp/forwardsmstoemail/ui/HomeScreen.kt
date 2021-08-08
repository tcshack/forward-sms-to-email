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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsWithImePadding
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
    var errors by remember { mutableStateOf(SettingsError()) }
    val focusManager = LocalFocusManager.current

    applyPreferencesUiState.settingsError?.let {
        errors = it
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
            imeAction = ImeAction.Next,
            label = "Phone Number",
            value = phoneNumber,
            errorMessageResId = errors.phoneNumberErrorResId,
            focusManager = focusManager
        ) {
            viewModel.onPhoneNumberChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            label = "Email Address",
            value = emailAddress,
            errorMessageResId = errors.emailErrorResId,
            focusManager = focusManager
        ) {
            viewModel.onEmailAddressChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            label = "Email Password",
            value = emailPassword,
            errorMessageResId = errors.emailPasswordErrorResId,
            focusManager = focusManager,
            isPassword = true
        ) {
            viewModel.onEmailPasswordChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            label = "Mail Server",
            value = mailServer,
            errorMessageResId = errors.mailServerErrorResId,
            focusManager = focusManager
        ) {
            viewModel.onMailServerChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            label = "Mail Host",
            value = mailHost,
            errorMessageResId = errors.mailHostErrorResId,
            focusManager = focusManager
        ) {
            viewModel.onMailHostChange(it)
        }

        MaterialTextInput(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            label = "Mail Port",
            value = mailPort,
            errorMessageResId = errors.mailPortErrorResId,
            focusManager = focusManager
        ) {
            viewModel.onMailPortChange(it)
        }
    }
}

@Composable
fun MaterialTextInput(
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    label: String,
    value: String,
    errorMessageResId: Int?,
    focusManager: FocusManager,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsWithImePadding(),
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            maxLines = 1,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        errorMessageResId?.let { messageResId ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = messageResId),
                style = TextStyle(
                    color = Color.Red.copy(alpha = .6f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.productsansregular))
                )
            )
        }
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