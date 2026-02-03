package com.emc.moodmingle.ui.create.post.dialogs

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.UrlTextColor
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import org.jsoup.Jsoup

@Composable
fun CreatePostEventDialog(
    metadata: LinkMetadata?,
    onEventSelected: (LinkMetadata?) -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader(label = "Create Event", onBack = onDismiss) },
        modifier = Modifier.padding(top = 8.dp)
    ) { paddingValues ->
        EventDialogContent(paddingValues, metadata, onEventSelected, onDismiss)
    }
}

@Composable
fun EventDialogContent(
    paddingValues: PaddingValues,
    metadata: LinkMetadata?,
    onEventSelected: (LinkMetadata?) -> Unit,
    onDismiss: () -> Unit
) {
    var link by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = link,
            onValueChange = {
                link = it
                error = null
            },
            label = { Text("Type or paste event link...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = Color.White,
                focusedTextColor = UrlTextColor,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        PreviewButton(link, onEventSelected, onLoading = { loading = it }) { error = it }

        Spacer(Modifier.height(16.dp))

        when {
            loading -> CircularProgressIndicator(modifier = Modifier.drawGradient())
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            metadata != null -> EventPreviewCard(
                metadata = metadata,
                onConfirm = { onEventSelected(metadata); onDismiss() }
            )
        }
    }
}

@Composable
fun PreviewButton(
    link: String,
    onEventSelected: (LinkMetadata?) -> Unit,
    onLoading: (Boolean) -> Unit,
    onError: (String?) -> Unit
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                onLoading(true)
                onError(null)
                try {
                    onEventSelected(scrapeLinkMetadata(link))
                } catch (_: Exception) {
                    onError("FAILED TO LOAD EVENT PREVIEW")
                }
                onLoading(false)
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = UrlTextColor,
            contentColor = Color.White
        ),
        enabled = link.startsWith("http"),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("PREVIEW", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EventPreviewCard(metadata: LinkMetadata, onConfirm: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryDark)
    ) {
        Column {
            metadata.imageUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {

                metadata.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                }

                metadata.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                metadata.siteName?.let {
                    Text(
                        text = it.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = UrlTextColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UrlTextColor,
                        contentColor = Color.White
                    )
                ) {
                    Text("ADD EVENT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Fetches metadata from a given URL using jsoup.
 * This function extracts Open Graph tags with fallbacks.
 */
suspend fun scrapeLinkMetadata(url: String): LinkMetadata = withContext(Dispatchers.IO) {
    val document = Jsoup.connect(url)
        .userAgent("Mozilla/5.0 (Android)")
        .timeout(10_000)
        .followRedirects(true)
        .get()

    fun og(property: String): String? =
        document.selectFirst("meta[property=$property]")?.attr("content")

    fun meta(name: String): String? = document.selectFirst("meta[name=$name]")?.attr("content")

    val title = og("og:title") ?: document.title().takeIf { it.isNotBlank() }
    val description = og("og:description") ?: meta("description")
    val image = og("og:image") ?: document.selectFirst("img")?.absUrl("src")
    val siteName = og("og:site_name") ?: url.toUri().host

    LinkMetadata(
        title = title,
        description = description,
        imageUrl = image,
        siteName = siteName,
        url = url
    )
}


/**
 * Represents metadata extracted from a URL using jsoup.
 */
@Parcelize
data class LinkMetadata(
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val siteName: String? = null,
    val url: String = ""
) : Parcelable
