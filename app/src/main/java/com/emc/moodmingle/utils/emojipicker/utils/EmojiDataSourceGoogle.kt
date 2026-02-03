package com.emc.moodmingle.utils.emojipicker.utils

import com.vanniktech.emoji.google.GoogleEmojiProvider
import emoji.core.datasource.EmojiDataSource
import emoji.core.model.NetworkEmoji
import java.io.File

class EmojiDataSourceGoogle : EmojiDataSource {

    override suspend fun getAllEmojis(
        cacheFile: File?
    ): List<NetworkEmoji> {

        val provider = GoogleEmojiProvider()

        return provider.categories.flatMap { category ->

            val groupName = category.categoryNames["en"] ?: "Other"

            category.emojis.map { googleEmoji ->

                NetworkEmoji(
                    character = googleEmoji.unicode,
                    codePoint = googleEmoji.unicode.toCodePointString(),
                    group = groupName,
                    subgroup = "", // google does not expose subgroup
                    unicodeName = googleEmoji.shortcodes.firstOrNull()
                        ?.replace("_", " ")
                        ?: ""
                )
            }
        }
    }
}

private fun String.toCodePointString(): String =
    this.codePoints()
        .toArray()
        .joinToString(" ") { cp ->
            cp.toString(16).uppercase()
        }
