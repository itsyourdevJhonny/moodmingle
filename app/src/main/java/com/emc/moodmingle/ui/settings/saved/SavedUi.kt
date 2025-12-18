package com.emc.moodmingle.ui.settings.saved

import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.saved.SaveEntityFirebase
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase

data class SavedUi(
    val save: SaveEntityFirebase,
    val post: PostEntityFirebase?,
    val user: UserEntityFirebase?
)
