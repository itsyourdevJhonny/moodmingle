package com.emc.moodmingle.ui.settings.password

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.local.UserViewModel

@Composable
fun PasswordScreen(onBack: () -> Unit, onChange: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 26.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PasswordHeaderIcon(onBack)
        PasswordContent(onChange)
    }
}

@Composable
fun PasswordHeaderIcon(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = BrushPrimaryGradient),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            text = "Password",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun PasswordContent(onChange: () -> Unit) {
    val userViewModel = hiltViewModel<UserViewModel>()
    val loggedUser by userViewModel.getLoggedUserByUid().collectAsState(initial = null)
    val currentPassword = loggedUser?.password ?: ""

    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Current Password",
            color = Color.White
        )

        TextField(
            value = currentPassword,
            onValueChange = {},
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledLeadingIconColor = Color.White,
                disabledTrailingIconColor = Color.White,
                disabledTextColor = GrayTextColor
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = CircleShape,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { PasswordIcon() },
            trailingIcon = {
                Row {
                    VisibilityButton(
                        isPasswordVisible,
                        onPasswordVisible = { isPasswordVisible = it }
                    )
                    CopyButton(currentPassword)
                }
            }
        )

        TextButton(
            onClick = onChange,
            modifier = Modifier
                .align(Alignment.End)
                .background(BrushPrimaryGradient, CircleShape)
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = "Change",
                tint = Color.White
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Change",
                color = Color.White
            )
        }
    }
}

@Composable
fun PasswordIcon() {
    Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Password",
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
    )
}

@Composable
fun VisibilityButton(isPasswordVisible: Boolean, onPasswordVisible: (Boolean) -> Unit) {
    val iconRes = if (isPasswordVisible) R.drawable.visibility_on
    else R.drawable.visibility_off

    IconButton(onClick = { onPasswordVisible(!isPasswordVisible) }) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(iconRes),
            contentDescription = "Visibility"
        )
    }
}

@Composable
fun CopyButton(currentPassword: String) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", currentPassword)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
        },
        content = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.bio),
                contentDescription = "Copy"
            )
        }
    )
}

