package com.emc.moodmingle.ui.post.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun ExpandableAutoDetectClickableText(
    fullText: String,
    style: TextStyle,
    hasPadding: Boolean,
    onMentionClick: (String) -> Unit = {},
    onHashtagClick: (String) -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var highlightedRange by remember { mutableStateOf<IntRange?>(null) }
    var isTapped by remember { mutableStateOf(false) }

    // animation feedback when tapped
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 0.97f else 1f,
        animationSpec = tween(120),
        label = "tap_scale"
    )

    val annotatedText = remember(fullText, highlightedRange) {
        buildAnnotatedString {
            val mentionPattern = Pattern.compile("@\\w+")
            val hashtagPattern = Pattern.compile("#\\w+")
            val emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val phonePattern = Pattern.compile("\\+?\\d{7,15}")
            val urlPattern = Patterns.WEB_URL

            val allMatches = mutableListOf<MatchRange>()

            fun collect(pattern: Pattern, type: MatchType) {
                val matcher = pattern.matcher(fullText)
                while (matcher.find()) {
                    allMatches.add(MatchRange(matcher.start(), matcher.end(), matcher.group() ?: "", type))
                }
            }

            collect(mentionPattern, MatchType.MENTION)
            collect(hashtagPattern, MatchType.HASHTAG)
            collect(emailPattern, MatchType.EMAIL)
            collect(phonePattern, MatchType.PHONE)
            collect(urlPattern, MatchType.URL)

            allMatches.sortBy { it.start }

            // remove overlaps
            val filtered = mutableListOf<MatchRange>()
            var lastEnd = -1
            for (m in allMatches) {
                if (m.start >= lastEnd) {
                    filtered.add(m)
                    lastEnd = m.end
                }
            }

            var currentIndex = 0
            for (m in filtered) {
                if (currentIndex < m.start) append(fullText.substring(currentIndex, m.start))

                val isHighlighted = highlightedRange?.let { m.start in it || m.end in it } == true

                val color = when (m.type) {
                    MatchType.MENTION -> Color(0xFF81C784)
                    MatchType.HASHTAG -> Color(0xFFFFB74D)
                    MatchType.EMAIL -> Color(0xFFBA68C8)
                    MatchType.PHONE -> Color(0xFFE57373)
                    MatchType.URL -> Color(0xFF64B5F6)
                }

                val background = if (isHighlighted) color.copy(alpha = 0.25f) else Color.Transparent

                pushStringAnnotation(tag = m.type.name, annotation = m.value)
                withStyle(SpanStyle(
                    color = color,
                    background = background,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 16.sp
                )) {
                    append(m.value)
                }
                pop()
                currentIndex = m.end
            }

            if (currentIndex < fullText.length) append(fullText.substring(currentIndex))
        }
    }

    Text(
        text = annotatedText,
        style = style,
        maxLines = if (expanded) Int.MAX_VALUE else 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(
                if (hasPadding) PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp)
                else PaddingValues(0.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        expanded = !expanded // toggle expand/collapse anywhere
                    },
                    onLongPress = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("text", fullText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
                    },
                    onPress = {
                        isTapped = true
                        tryAwaitRelease()
                        isTapped = false
                    }
                )
            },
        onTextLayout = {}
    )
}

// Match container and type
private data class MatchRange(val start: Int, val end: Int, val value: String, val type: MatchType)
private enum class MatchType { MENTION, HASHTAG, EMAIL, PHONE, URL }
/*
@Composable
fun AutoDetectClickableText(
    text: String,
    style: TextStyle,
    hasPadding: Boolean,
    onMentionClick: (String) -> Unit = {},
    onHashtagClick: (String) -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var highlightedRange by remember { mutableStateOf<IntRange?>(null) }
    var isTapped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // animation feedback when tapped
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 0.97f else 1f,
        animationSpec = tween(120),
        label = "tap_scale"
    )

    val annotatedText = remember(text, highlightedRange) {
        buildAnnotatedString {
            // define patterns
            val mentionPattern = Pattern.compile("@\\w+")
            val hashtagPattern = Pattern.compile("#\\w+")
            val emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val phonePattern = Pattern.compile("\\+?\\d{7,15}")
            val urlPattern = Patterns.WEB_URL

            val allMatches = mutableListOf<MatchRange>()

            // helper to collect all matches
            fun collect(pattern: Pattern, type: MatchType) {
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    allMatches.add(
                        MatchRange(
                            start = matcher.start(),
                            end = matcher.end(),
                            value = matcher.group() ?: "",
                            type = type
                        )
                    )
                }
            }

            // collect in order of priority
            collect(mentionPattern, MatchType.MENTION)
            collect(hashtagPattern, MatchType.HASHTAG)
            collect(emailPattern, MatchType.EMAIL)
            collect(phonePattern, MatchType.PHONE)
            collect(urlPattern, MatchType.URL)

            // sort by start index
            allMatches.sortBy { it.start }

            // remove overlapping matches
            val filtered = mutableListOf<MatchRange>()
            var lastEnd = -1
            for (match in allMatches) {
                if (match.start >= lastEnd) {
                    filtered.add(match)
                    lastEnd = match.end
                }
            }

            // build styled text
            var currentIndex = 0
            for (match in filtered) {
                if (currentIndex < match.start) {
                    append(text.substring(currentIndex, match.start))
                }

                val isHighlighted = highlightedRange?.let {
                    match.start in it || match.end in it
                } == true

                val color = when (match.type) {
                    MatchType.MENTION -> Color(0xFF81C784)
                    MatchType.HASHTAG -> Color(0xFFFFB74D)
                    MatchType.EMAIL -> Color(0xFFBA68C8)
                    MatchType.PHONE -> Color(0xFFE57373)
                    MatchType.URL -> Color(0xFF64B5F6)
                }

                val background = if (isHighlighted) color.copy(alpha = 0.25f) else Color.Transparent

                pushStringAnnotation(tag = match.type.name, annotation = match.value)
                withStyle(
                    SpanStyle(
                        color = color,
                        background = background,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp
                    )
                ) {
                    append(match.value)
                }
                pop()

                currentIndex = match.end
            }

            if (currentIndex < text.length) {
                append(text.substring(currentIndex))
            }
        }
    }

    Text(
        text = annotatedText,
        style = style,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(
                if (hasPadding) PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ) else PaddingValues(0.dp)
            )
            .onGloballyPositioned { }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("text", annotatedText)
                        clipboardManager.setPrimaryClip(clipData)

                        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onPress = {
                        isTapped = true
                        tryAwaitRelease()
                        isTapped = false
                    },
                    onTap = { offset ->
                        layoutResult?.let { layout ->
                            val pos = layout.getOffsetForPosition(offset)
                            annotatedText.getStringAnnotations(start = pos, end = pos)
                                .firstOrNull()?.let { annotation ->
                                    val range = findMatchRange(annotation.item, text)
                                    highlightedRange = range
                                    scope.launch {
                                        delay(150)
                                        highlightedRange = null
                                    }

                                    when (annotation.tag) {
                                        MatchType.MENTION.name -> onMentionClick(annotation.item)
                                        MatchType.HASHTAG.name -> onHashtagClick(annotation.item)
                                        MatchType.EMAIL.name -> {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = "mailto:${annotation.item}".toUri()
                                            }
                                            context.startActivity(intent)
                                        }

                                        MatchType.PHONE.name -> {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = "tel:${annotation.item}".toUri()
                                            }
                                            context.startActivity(intent)
                                        }

                                        MatchType.URL.name -> uriHandler.openUri(annotation.item)
                                    }
                                }
                        }
                    }
                )
            },
        onTextLayout = { layoutResult = it },
        maxLines = Int.MAX_VALUE,
        overflow = TextOverflow.Visible
    )
}

// find range of clicked match for highlight
private fun findMatchRange(target: String, fullText: String): IntRange? {
    val start = fullText.indexOf(target)
    return if (start >= 0) start until start + target.length else null
}

// match range container
private data class MatchRange(
    val start: Int,
    val end: Int,
    val value: String,
    val type: MatchType
)

// supported match types
private enum class MatchType {
    MENTION, HASHTAG, EMAIL, PHONE, URL
}*/
