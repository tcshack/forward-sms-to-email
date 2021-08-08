package com.tcscorp.forwardsmstoemail.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val preferencesFlow: Flow<Result<Settings>> = flow {
        try {
            context.dataStore.data
                .collect { preferences ->
                    val phoneNumber = preferences[PreferencesKeys.KEY_PHONE_NUMBER] ?: ""
                    val emailAddress = preferences[PreferencesKeys.KEY_EMAIL_ADDRESS] ?: ""
                    val emailPassword = preferences[PreferencesKeys.KEY_EMAIL_PASSWORD] ?: ""
                    val mailServer = preferences[PreferencesKeys.KEY_MAIL_SERVER] ?: ""
                    val mailHost = preferences[PreferencesKeys.KEY_MAIL_HOST] ?: ""
                    val mailPort = preferences[PreferencesKeys.KEY_MAIL_PORT]?.toString() ?: ""
                    emit(
                        Result.Success(
                            Settings(
                                phoneNumber,
                                emailAddress,
                                emailPassword,
                                mailServer,
                                mailHost,
                                mailPort
                            )
                        )
                    )
                }
        } catch (error: Exception) {
            emit(Result.Error(error))
        }
    }

    suspend fun applySettings(settings: Settings): Flow<Result<Unit>> {
        return flow {
            try {
                context.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.KEY_PHONE_NUMBER] = settings.phoneNumber
                    preferences[PreferencesKeys.KEY_EMAIL_ADDRESS] = settings.emailAddress
                    preferences[PreferencesKeys.KEY_EMAIL_PASSWORD] = settings.emailPassword
                    preferences[PreferencesKeys.KEY_MAIL_SERVER] = settings.mailServer
                    preferences[PreferencesKeys.KEY_MAIL_HOST] = settings.mailHost
                    preferences[PreferencesKeys.KEY_MAIL_PORT] = settings.mailPort.toInt()
                    emit(Result.Success(Unit))
                }
            } catch (error: Exception) {
                emit(Result.Error(error))
            }
        }
    }

    private object PreferencesKeys {
        val KEY_PHONE_NUMBER = stringPreferencesKey("phone_number")
        val KEY_EMAIL_ADDRESS = stringPreferencesKey("email_address")
        val KEY_EMAIL_PASSWORD = stringPreferencesKey("email_password")
        val KEY_MAIL_SERVER = stringPreferencesKey("mail_server")
        val KEY_MAIL_HOST = stringPreferencesKey("mail_host")
        val KEY_MAIL_PORT = intPreferencesKey("mail_port")
    }

}