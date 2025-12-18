package com.emc.moodmingle.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.cloudinary.CloudinaryService
import com.emc.moodmingle.ui.profile.DrawUserNoPaddingLine
import com.emc.moodmingle.ui.settings.personal.ImageGallery
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.PurpleDark
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.LoadingDialog
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.UserViewModelFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onLoginClick: () -> Unit, onRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var uploadedUri by remember { mutableStateOf<Uri?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(PurplePrimary, PurpleDark))),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(SecondaryDark, shape = RoundedCornerShape(16.dp))
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegisterSetupTitle()

                RegisterField(
                    value = username,
                    label = "Username",
                    onValueChange = { username = it },
                    type = "USERNAME"
                )

                RegisterField(
                    value = email,
                    label = "Email",
                    onValueChange = { email = it },
                    type = "EMAIL"
                )

                RegisterField(
                    value = password,
                    label = "Password",
                    onValueChange = { password = it },
                    type = "PASSWORD"
                )

                Spacer(modifier = Modifier.height(12.dp))

                SetupAvatar(onUploadedUri = { uploadedUri = it })

                Column {
                    RegisterBio(bio, onValueChange = { bio = it })

                    Spacer(modifier = Modifier.height(15.dp))

                    RegisterButton(
                        username = username,
                        email = email,
                        password = password,
                        bio = bio,
                        uploadedUri = uploadedUri,
                        onLoginClick = onLoginClick,
                        onRegister = onRegister
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterSetupTitle() {
    Text(
        "Register",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            brush = Brush.linearGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
        ),
        textAlign = TextAlign.Center
    )
    Text(
        text = "Set up your profile to start sharing your moods",
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 7.dp, bottom = 20.dp),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun RegisterField(value: String, label: String, onValueChange: (String) -> Unit, type: String) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        ),
        visualTransformation = if (type == "PASSWORD") {
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (type == "PASSWORD" && value.trim().isNotEmpty()) {
                val iconRes = if (isPasswordVisible) R.drawable.visibility_on
                else R.drawable.visibility_off

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(iconRes),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = Color.White
                    )
                }
            }
        },
        leadingIcon = {
            val imageVector = when (type) {
                "USERNAME" -> Icons.Default.AccountCircle
                "EMAIL" -> Icons.Default.Email
                "PASSWORD" -> Icons.Default.Lock
                else -> Icons.Default.AccountCircle
            }

            Icon(
                imageVector = imageVector,
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient(),
                contentDescription = type
            )
        }
    )
}

@Composable
fun SetupAvatar(onUploadedUri: (Uri?) -> Unit) {
    var isShowDialog by remember { mutableStateOf(false) }
    var isUploaded by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    if (isUploaded && fileUri != null) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = fileUri,
                contentDescription = "Image",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { fileUri = null },
                modifier = Modifier.align(Alignment.TopCenter).padding(start = 88.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Red,
                )
            }
        }
    } else {
        Button(
            onClick = { isShowDialog = true },
            modifier = Modifier
                .padding(bottom = 4.dp)
                .background(brush = BrushPrimaryGradient, shape = CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            content = {
                Icon(
                    painter = painterResource(R.drawable.upload_image),
                    contentDescription = "Image",
                    tint = Color.White
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Upload Image",
                    color = Color.White
                )
            }
        )
    }

    if (isShowDialog) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 22.dp)
                    .background(PrimaryDark),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        if (isSelected) isSelected = false
                        isShowDialog = false
                    },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back Icon",
                        tint = Color.White
                    )
                }

                AsyncImage(
                    model = fileUri,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                DrawUserNoPaddingLine(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 10.dp
                    )
                )

                if (isSelected && fileUri != null) {
                    TextButton(
                        onClick = {
                            isUploaded = true
                            isShowDialog = false
                        },
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .background(BrushPrimaryGradient, RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.upload_image),
                            contentDescription = "Upload",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        text = "Choose image as your avatar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GrayTextColor
                        )
                    )
                }

                ImageGallery(
                    onSelectedImage = { uri, uploaded ->
                        fileUri = uri
                        isSelected = uploaded
                    }
                )
            }
        }
    }

    if (isUploaded) {
        onUploadedUri(fileUri)
    }
}

@Composable
fun RegisterBio(bio: String, onValueChange: (String) -> Unit) {
    Text(
        text = "Bio (Optional)",
        textAlign = TextAlign.Left,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    OutlinedTextField(
        value = bio,
        onValueChange = onValueChange,
        label = { Text("Tell others about yourself...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = GrayTextColor,
            focusedLabelColor = Color.White,
            focusedBorderColor = PurplePrimary,
            focusedTextColor = Color.White
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.bio),
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
                    },
                contentDescription = "Bio"
            )
        }
    )
}

@Composable
fun RegisterButton(
    username: String,
    email: String,
    password: String,
    bio: String,
    uploadedUri: Uri?,
    onLoginClick: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current
    val userViewModelFirebase = hiltViewModel<UserViewModelFirebase>()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            if (username.isBlank()) {
                Toast.makeText(context, "Please enter username", Toast.LENGTH_LONG).show()
                return@Button
            }

            if (email.isBlank()) {
                Toast.makeText(context, "Please enter email", Toast.LENGTH_LONG).show()
                return@Button
            }

            if (uploadedUri == null) {
                Toast.makeText(context, "Please upload an image", Toast.LENGTH_LONG).show()
                return@Button
            }

            if (password.isBlank()) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_LONG).show()
                return@Button
            }

            isLoading = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(BrushPrimaryGradient, CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Register",
                tint = Color.White
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Register",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    HaveAccountSection(onLoginClick)

    if (isLoading) {
        LoadingDialog("Creating your account") {
            scope.launch {
                try {
                    val uploadedUrl = CloudinaryService.uploadFile(context, uploadedUri)

                    userViewModelFirebase.register(
                        username = username,
                        email = email,
                        password = password,
                        avatarUrl = uploadedUrl ?: "",
                        onSuccess = {
                            isLoading = false

                            Toast.makeText(
                                context,
                                "Account registered successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            onRegister()
                        },
                        onError = { message ->
                            isLoading = false
                            Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                        }
                    )
                } catch (e: Exception) {
                    isLoading = false
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@Composable
fun HaveAccountSection(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White
            )
        )

        Text(
            modifier = Modifier.clickable { onLoginClick() },
            text = "Login",
            color = PurplePrimary,
            fontStyle = FontStyle.Italic
        )
    }
}