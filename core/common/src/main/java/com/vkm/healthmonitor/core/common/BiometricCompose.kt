package com.vkm.healthmonitor.core.common

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricLauncher(
    private val activity: FragmentActivity,
    private val onAuthenticationSucceeded: (androidx.biometric.BiometricPrompt.AuthenticationResult) -> Unit,
    private val onAuthenticationError: (Int, String) -> Unit,
    private val onAuthenticationFailed: () -> Unit
) {
    fun authenticate(promptInfo: androidx.biometric.BiometricPrompt.PromptInfo) {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(activity)
        val biometricPrompt = androidx.biometric.BiometricPrompt(activity, executor, object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onAuthenticationError(errorCode, errString.toString())
            }
            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                this@BiometricLauncher.onAuthenticationSucceeded(result)
            }
            override fun onAuthenticationFailed() {
                onAuthenticationFailed()
            }
        })
        biometricPrompt.authenticate(promptInfo)
    }
}
