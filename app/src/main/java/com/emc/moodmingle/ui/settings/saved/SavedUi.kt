package com.emc.moodmingle.ui.settings.saved

import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase

data class SavedUi(
    val save: SaveEntityFirebase,
    val post: PostEntityFirebase?,
    val user: UserEntityFirebase?
)
