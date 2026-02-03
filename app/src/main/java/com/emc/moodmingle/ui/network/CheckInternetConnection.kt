package com.emc.moodmingle.ui.network

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.utils.network.NetworkStatus
import com.emc.moodmingle.utils.network.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInternetConnection(networkUtils: NetworkUtils, content: @Composable () -> Unit) {
    val context = LocalContext.current
    var networkStatus by remember { mutableStateOf<NetworkStatus>(NetworkStatus.Connected) }
    val scope = rememberCoroutineScope()

    fun retryConnection() {
        scope.launch {
            delay(1000)
            networkStatus =
                if (networkUtils.isInternetAvailable()) NetworkStatus.Connected else NetworkStatus.NoInternet
        }
    }

    LaunchedEffect(Unit) {
        networkUtils.observeNetworkChanges().collectLatest { status ->
            networkStatus = status
        }
    }

    when (networkStatus) {
        is NetworkStatus.NoInternet -> {
            AlertDialog(
                onDismissRequest = {},
                containerColor = PrimaryDark,
                title = {
                    AlertDialogTitle(
                        "NO INTERNET CONNECTION",
                        R.drawable.no_internet,
                        Color.Red
                    )
                },
                text = { Text("Please check your Wi-Fi or mobile data connection.") },
                confirmButton = { AlertDialogConfirmButton(label = "RETRY") { retryConnection() } },
                shape = RectangleShape
            )
        }

        is NetworkStatus.SlowInternet -> {
            Toast.makeText(context, "Your connection seems slow. Some features may not work properly.", Toast.LENGTH_SHORT).show()
        }

        is NetworkStatus.Connected -> {
            Toast.makeText(context, "Connected to MoodMingle", Toast.LENGTH_SHORT).show()
        }
    }

    content()
}

@Composable
fun AlertDialogTitle(
    title: String,
    @DrawableRes iconRes: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(end = 8.dp),
            painter = painterResource(iconRes),
            contentDescription = "Network status",
            tint = color
        )
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
fun AlertDialogConfirmButton(label: String, retryConnection: () -> Unit) {
    Button(
        onClick = { retryConnection() },
        modifier = Modifier
            .fillMaxWidth()
            .background(BrushPrimaryGradient, RoundedCornerShape(30.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", tint = Color.White)
        Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
