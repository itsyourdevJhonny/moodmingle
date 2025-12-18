package com.emc.moodmingle.cloudinary

/**
 * Data class to store the upload result from Cloudinary.
 */
data class UploadResult(
    val url: String?,        // The uploaded file's secure URL
    val publicId: String?    // The unique public ID assigned by Cloudinary
)
