package com.emc.moodmingle.utils

import java.security.MessageDigest

fun encryptData(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}