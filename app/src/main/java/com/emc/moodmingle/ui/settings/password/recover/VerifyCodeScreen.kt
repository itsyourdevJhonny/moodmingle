package com.emc.moodmingle.ui.settings.password.recover

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.ui.settings.password.utils.TopIcon
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.utils.LoadingDialog
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch
import kotlin.String

@Composable
fun VerifyCodeScreen(email: String, onVerified: () -> Unit) {
    val codeLength = 6
    val codeDigits = remember { Array(codeLength) { mutableStateOf("") } }
    var isLoading by remember { mutableStateOf(false) }

    val focusRequesters = remember { Array(codeLength) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 128.dp),
            content = { TopIcon(imageVector = Icons.Default.Check, size = 88.dp) }
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter the verification code sent to $email",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                codeDigits.forEachIndexed { index, digitState ->
                    OutlinedTextField(
                        value = digitState.value,
                        onValueChange = { value ->
                            // accept only digits and max length of 1
                            if (value.length <= 1 && value.all { it.isDigit() }) {
                                digitState.value = value

                                // move focus to next when a value is entered
                                if (value.isNotEmpty() && index < codeLength - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }

                                // if all filled, clear focus
                                if (codeDigits.all { it.value.isNotEmpty() }) {
                                    focusManager.clearFocus()
                                }
                            } else if (value.isEmpty()) {
                                // move focus back when deleting
                                digitState.value = ""
                                if (index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }
                            }
                        },
                        modifier = Modifier
                            .width(48.dp)
                            .focusRequester(focusRequesters[index]),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (index == codeLength - 1) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < codeLength - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            },
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = PurplePrimary
                        ),
                        shape = CircleShape
                    )
                }
            }

            VerifyButton(
                codeDigits = codeDigits,
                codeLength = codeLength,
                email = email,
                onVerified = onVerified,
                isLoading = isLoading,
                onLoadingChange = { isLoading = it }
            )

            ResendButton(email)
        }
    }
}

@Composable
fun ResendButton(email: String) {
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()

    var resending by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Text(
        text = "Didn't receive a code?",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
        )
    )
    TextButton(
        onClick = {
            resending = true
        }
    ) {
        Text(
            text = "Resend",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = BrushPrimaryGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }

        )
    }

    if (resending) {
        LoadingDialog("Resending code") {
            scope.launch {
                val result = firebaseUserViewModel.sendVerificationCode(email)
                result.onSuccess { exist ->
                    if (exist) {
                        Toast.makeText(context, "Code resent to $email", Toast.LENGTH_SHORT).show()
                        Toast.makeText(context, "Please check your email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Email does not exist", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure {
                    Toast.makeText(
                        context,
                        "Failed to send code: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                resending = false
            }
        }
    }
}

@Composable
fun VerifyButton(
    codeDigits: Array<MutableState<String>>,
    codeLength: Int,
    email: String,
    onVerified: () -> Unit,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val firebaseUserViewModel = hiltViewModel<FirebaseUserViewModel>()
    val scope = rememberCoroutineScope()
    val code = codeDigits.joinToString("") { it.value }

    Button(
        onClick = {
            if (code.length < codeLength) {
                Toast.makeText(context, "Enter full code", Toast.LENGTH_SHORT).show()
                return@Button
            }

            onLoadingChange(true)
        },
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
            .background(BrushPrimaryGradient, CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Continue",
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Verify Code",
                color = Color.White
            )
        }
    }

    if (isLoading) {
        LoadingDialog("Verifying code") {
            scope.launch {
                val result = firebaseUserViewModel.verifyCode(email, code)

                onLoadingChange(false)

                result.onSuccess { verified ->
                    if (verified) onVerified()
                    else Toast.makeText(context, "Invalid code", Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}