package com.nice.balafm.util

internal fun validatePhoneNumber(phoneNumber: String): Boolean {
    val regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$"//手机号的正则表达式
    return phoneNumber.matches(regex.toRegex())
}

internal fun validatePassword(password: String): Boolean {
    val regex = "^[0-9A-Za-z]{6,20}\$"
    return password.matches(regex.toRegex())
}

internal fun validateVerificationCode(code: String): Boolean {
    val regex = "^\\d{6}\$"
    return code.matches(regex.toRegex())
}

fun getFormatTime(_time: Long): String {
    val time = _time / 1000
    val second = time % 60
    val minute = time % 3600 / 60
    val hour = time / 3600

    val strSecond = ("00" + second).substring(("00" + second).length - 2)

    val strMinute = ("00" + minute).substring(("00" + minute).length - 2)

    val strHour = ("00" + hour).substring(("00" + hour).length - 2)

    return "$strHour : $strMinute : $strSecond"
}