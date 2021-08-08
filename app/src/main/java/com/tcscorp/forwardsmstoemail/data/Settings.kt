package com.tcscorp.forwardsmstoemail.data

data class Settings(
    val phoneNumber: String = "",
    val emailAddress: String = "",
    val emailPassword: String = "",
    val mailServer: String = "",
    val mailHost: String = "",
    val mailPort: String = ""
)