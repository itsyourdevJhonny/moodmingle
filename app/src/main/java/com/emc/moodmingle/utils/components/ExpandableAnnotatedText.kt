package com.emc.moodmingle.utils.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.EmailTextColor
import com.emc.moodmingle.ui.theme.HashtagTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PhoneTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.theme.UrlTextColor
import java.util.regex.Pattern

@Composable
fun ExpandableAnnotatedText(
    fullText: String,
    style: TextStyle = Typography.bodyLarge,
    minLines: Int = 1
) {
    val context = LocalContext.current
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
                    allMatches.add(
                        MatchRange(
                            matcher.start(),
                            matcher.end(),
                            matcher.group() ?: "",
                            type
                        )
                    )
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
                    MatchType.MENTION -> MentionTextColor
                    MatchType.HASHTAG -> HashtagTextColor
                    MatchType.EMAIL -> EmailTextColor
                    MatchType.PHONE -> PhoneTextColor
                    MatchType.URL -> UrlTextColor
                }

                val background = if (isHighlighted) color.copy(alpha = 0.25f) else Color.Transparent

                pushStringAnnotation(tag = m.type.name, annotation = m.value)

                withStyle(
                    SpanStyle(
                        color = color,
                        background = background,
                        fontWeight = if (color == MentionTextColor) FontWeight.Black else FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        textDecoration = if (color == MentionTextColor) TextDecoration.None else TextDecoration.Underline,
                        fontSize = 16.sp
                    )
                ) {
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
        maxLines = if (expanded) Int.MAX_VALUE else minLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { expanded = !expanded },
                    onLongPress = {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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

private data class MatchRange(val start: Int, val end: Int, val value: String, val type: MatchType)
private enum class MatchType { MENTION, HASHTAG, EMAIL, PHONE, URL }