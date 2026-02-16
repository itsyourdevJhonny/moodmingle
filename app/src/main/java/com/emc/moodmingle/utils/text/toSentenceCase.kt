package com.emc.moodmingle.utils.text

import kotlin.text.split

/**
 * Converts a string into a readable sentence-style format.
 *
 * This function:
 * 1. Replaces all non-alphanumeric symbols (e.g., `_`, `-`, `.`, etc.) with spaces.
 * 2. Splits camelCase boundaries (e.g., "textOneTwo" → "text One Two").
 * 3. Normalizes each word to lowercase.
 * 4. Capitalizes the first character of each word.
 *
 * Examples:
 * - "text1_text2"          → "Text1 Text2"
 * - "text1-text2"          → "Text1 Text2"
 * - "textOneTwo"           → "Text One Two"
 * - "TEXTOneTwo"           → "Text One Two"
 * - "text_oneTwo-TEST.val" → "Text One Two Test Val"
 * - "text1"                → "Text1"
 *
 * @receiver The original string to format.
 * @return A formatted, human-readable sentence-style string.
 */
fun String.toSentenceCase(): String {

    // Return early if string is blank to avoid unnecessary processing
    if (this.isBlank()) return this

    /** Step 1:
     *
     * Replace all non-alphanumeric characters with a space.
     *
     * This converts symbols like "_", "-", ".", etc., into word separators.
     */
    val cleaned = this.replace(Regex("[^A-Za-z0-9]"), " ")

    /** Step 2:
     *
     * Insert a space between lowercase and uppercase letter boundaries.
     *
     * Example: "textOne" → "text One"
     */
    val camelSeparated = cleaned.replace(Regex("([a-z])([A-Z])"), "$1 $2")

    /** Step 3:
     *
     * Split by whitespace, remove empty entries, normalize casing,
     *
     * and capitalize the first letter of each word.
     */
    return camelSeparated
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.titlecase() } }
}
