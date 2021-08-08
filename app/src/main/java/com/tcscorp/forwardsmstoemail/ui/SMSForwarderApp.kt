package com.tcscorp.forwardsmstoemail.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.tcscorp.forwardsmstoemail.ui.theme.SMSForwarderTheme

@Composable
fun SMSForwarderApp() {
    ProvideWindowInsets {
        SMSForwarderTheme {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(viewModel)
        }
    }
}
