package com.example.googlelogindemo.corestuff

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import java.util.regex.Matcher
import java.util.regex.Pattern

@ExperimentalPermissionsApi
fun PermissionState.isPermanentlyDenied(): Boolean {
    return !shouldShowRationale && !hasPermission
}

fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun CharSequence?.isValidPassword(): Boolean {
    val pattern: Pattern = Pattern.compile("""^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$""")
    val matcher: Matcher = pattern.matcher(this)

    return matcher.matches()
}
