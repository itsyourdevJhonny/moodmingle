package com.emc.moodmingle.utils.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun copyText(text: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
}