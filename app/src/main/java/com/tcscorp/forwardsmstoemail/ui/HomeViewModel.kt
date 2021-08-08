package com.tcscorp.forwardsmstoemail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcscorp.forwardsmstoemail.R
import com.tcscorp.forwardsmstoemail.data.Message
import com.tcscorp.forwardsmstoemail.data.PreferenceManager
import com.tcscorp.forwardsmstoemail.data.Result
import com.tcscorp.forwardsmstoemail.data.Settings
import com.tcscorp.forwardsmstoemail.domain.GetAllMessagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAllMessagesUseCase: GetAllMessagesUseCase,
    private val preferencesManager: PreferenceManager,
) : ViewModel() {

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _emailAddress = MutableStateFlow("")
    val emailAddress: StateFlow<String> = _emailAddress

    private val _emailPassword = MutableStateFlow("")
    val emailPassword: StateFlow<String> = _emailPassword

    private val _mailServer = MutableStateFlow("")
    val mailServer: StateFlow<String> = _mailServer

    private val _mailHost = MutableStateFlow("")
    val mailHost: StateFlow<String> = _mailHost

    private val _mailPort = MutableStateFlow("")
    val mailPort: StateFlow<String> = _mailPort

    private val _applyPreferences = MutableStateFlow(ApplyPreferencesUiState())
    val applyPreferences: StateFlow<ApplyPreferencesUiState> = _applyPreferences

    val onPhoneNumberChange: (String) -> Unit = { newPhoneNumber ->
        _phoneNumber.value = newPhoneNumber
    }

    val onEmailAddressChange: (String) -> Unit = { newEmailAddress ->
        _emailAddress.value = newEmailAddress
    }

    val onEmailPasswordChange: (String) -> Unit = { newEmailPassword ->
        _emailPassword.value = newEmailPassword
    }

    val onMailServerChange: (String) -> Unit = { newMailServer ->
        _mailServer.value = newMailServer
    }

    val onMailHostChange: (String) -> Unit = { newMailServer ->
        _mailHost.value = newMailServer
    }

    val onMailPortChange: (String) -> Unit = { newMailPort ->
        _mailPort.value = newMailPort
    }

    val preferences = preferencesManager.preferencesFlow
        .map { computePreferences(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            PreferencesUiState(true)
        )

    val messages: StateFlow<MessageUiState> =
        getAllMessagesUseCase(Unit)
            .map { compute(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                MessageUiState(true)
            )

    private fun compute(result: Result<List<Message>>): MessageUiState {
        return when (result) {
            is Result.Success -> MessageUiState(data = result.data)
            is Result.Error -> {
                result.throwable.printStackTrace()
                MessageUiState(error = result.throwable)
            }
        }
    }

    private fun computeApplyPreferences(result: Result<Unit>): ApplyPreferencesUiState {
        return when (result) {
            is Result.Success -> ApplyPreferencesUiState(data = Unit)
            is Result.Error -> {
                ApplyPreferencesUiState(error = result.throwable)
            }
        }
    }

    private fun computePreferences(result: Result<Settings>): PreferencesUiState {
        return when (result) {
            is Result.Success -> {
                _phoneNumber.value = result.data.phoneNumber
                _emailAddress.value = result.data.emailAddress
                _emailPassword.value = result.data.emailPassword
                _mailServer.value = result.data.mailServer
                _mailHost.value = result.data.mailHost
                _mailPort.value = result.data.mailPort
                PreferencesUiState(data = result.data)
            }
            is Result.Error -> {
                result.throwable.printStackTrace()
                PreferencesUiState(error = result.throwable)
            }
        }
    }

    fun applySettings() {
        val settings = Settings(
            _phoneNumber.value.trim(),
            _emailAddress.value.trim(),
            _emailPassword.value.trim(),
            _mailServer.value.trim(),
            _mailHost.value.trim(),
            _mailPort.value.trim()
        )
        viewModelScope.launch {
            val settingsError = validateSettings(settings)
            if (settingsError.hasError) {
                _applyPreferences.emit(ApplyPreferencesUiState(settingsError = settingsError))
                return@launch
            }
            _applyPreferences.emit(ApplyPreferencesUiState(true))
            val result = preferencesManager.applySettings(settings)
            result.collect { _applyPreferences.emit(computeApplyPreferences(it)) }
        }
    }

    private fun validateSettings(settings: Settings): SettingsError {
        val settingsError = SettingsError()
        when {
            settings.phoneNumber.isEmpty() -> settingsError.phoneNumberErrorResId =
                R.string.error_require_phone_number
            settings.emailAddress.isEmpty() -> settingsError.emailErrorResId =
                R.string.error_require_email_address
            settings.emailPassword.isEmpty() -> settingsError.emailPasswordErrorResId =
                R.string.error_require_email_password
            settings.mailServer.isEmpty() -> settingsError.mailServerErrorResId =
                R.string.error_require_server_host
            settings.mailHost.isEmpty() -> settingsError.mailHostErrorResId =
                R.string.error_require_mail_host
            settings.mailPort.isEmpty() -> settingsError.mailPortErrorResId =
                R.string.error_require_mail_port
        }
        return settingsError
    }
}

class SettingsError(
    var phoneNumberErrorResId: Int? = null,
    var emailErrorResId: Int? = null,
    var emailPasswordErrorResId: Int? = null,
    var mailServerErrorResId: Int? = null,
    var mailHostErrorResId: Int? = null,
    var mailPortErrorResId: Int? = null,
) {
    val hasError: Boolean
        get() = phoneNumberErrorResId != null ||
                emailErrorResId != null ||
                emailPasswordErrorResId != null ||
                mailServerErrorResId != null ||
                mailHostErrorResId != null ||
                mailPortErrorResId != null
}

class PreferencesUiState(
    isLoading: Boolean = false,
    data: Settings? = null,
    error: Throwable? = null,
) : UiState<Settings>(isLoading, data, error)


class ApplyPreferencesUiState(
    isLoading: Boolean = false,
    data: Unit? = null,
    error: Throwable? = null,
    val settingsError: SettingsError? = null
) : UiState<Unit>(isLoading, data, error)

class MessageUiState(
    isLoading: Boolean = false,
    data: List<Message>? = null,
    error: Throwable? = null
) : UiState<List<Message>>(isLoading, data, error)
