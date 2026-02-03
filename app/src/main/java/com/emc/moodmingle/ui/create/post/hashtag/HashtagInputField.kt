package com.emc.moodmingle.ui.create.post.hashtag

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.HashtagTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun HashtagInputField(hashtag: TextFieldValue, onHashtagChange: (TextFieldValue) -> Unit) {
    BasicTextField(
        value = hashtag,
        onValueChange = { newValue ->
            onHashtagChange(handleHashtagInput(hashtag, newValue))
        },
        cursorBrush = SolidColor(Color.White),
        textStyle = TextStyle(fontSize = 20.sp, color = Color.White),
        visualTransformation = HashtagVisualTransformation(hashtagColor = HashtagTextColor),
        decorationBox = { innerTextField ->
            Box {
                innerTextField()

                if (hashtag.text.isEmpty()) {
                    Text(text = "Tap to enter text...", fontSize = 16.sp, color = GrayTextColor)
                }
            }
        },
        modifier = Modifier
            .background(SecondaryDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .fillMaxWidth()
            .animateContentSize()
            .heightIn(min = 38.dp)
    )
}

private fun handleHashtagInput(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
    val oldText = old.text
    val newText = new.text
    var cursor = new.selection.start

    /* Detect enter (your working logic) */
    if (newText.length > oldText.length && newText.endsWith("\n")) {
        return new.copy(text = "$newText#", selection = TextRange(newText.length + 1))
    }

    /* Prevent removing first line hashtag */
    if (oldText.startsWith("#") && !newText.startsWith("#")) {
        return old
    }

    /* Process lines safely */
    val cleanedLines = newText.lines()
        .mapIndexedNotNull { index, rawLine ->
            var line = rawLine

            // remove empty hashtag lines except first
            if (index > 0 && line == "#") return@mapIndexedNotNull null

            // enforce hashtag prefix
            line = enforceHashtagPrefix(line)

            // prevent double ##
            line = sanitizeHashtagLine(line)

            line
        }

    val cleanedText = cleanedLines.joinToString("\n")

    /* Lock cursor after '#' */
    cursor = clampCursor(cleanedText, cursor)

    return new.copy(text = cleanedText, selection = TextRange(minOf(cursor, cleanedText.length)))
}

private fun clampCursor(text: String, cursor: Int): Int {
    val beforeCursor = text.take(cursor)
    val lineStart = beforeCursor.lastIndexOf('\n').let {
        if (it == -1) 0 else it + 1
    }

    // lock cursor to at least after '#'
    return maxOf(cursor, lineStart + 1)
}

private fun sanitizeHashtagLine(line: String): String {
    return when {
        line.startsWith("##") -> "#" + line.dropWhile { it == '#' }
        else -> line
    }
}

private fun enforceHashtagPrefix(line: String): String {
    if (line.isEmpty()) return "#"
    if (!line.startsWith("#")) return "#$line"
    return line
}

class HashtagVisualTransformation(private val hashtagColor: Color) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val builder = AnnotatedString.Builder()

        text.text.split("\n").forEachIndexed { index, line ->
            if (line.startsWith("#")) {
                builder.pushStyle(SpanStyle(color = hashtagColor))
                builder.append(line)
                builder.pop()
            } else {
                builder.append(line)
            }

            if (index != text.text.lines().lastIndex) {
                builder.append("\n")
            }
        }

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}