package com.emc.moodmingle.utils.online

import com.google.firebase.database.FirebaseDatabase

object PresenceManager {

    fun setUserOnline(userId: String) {
        val db = FirebaseDatabase.getInstance().getReference("usersStatus").child(userId)

        db.child("online").setValue(true)
        db.child("lastSeen").setValue(System.currentTimeMillis())

        db.child("online").onDisconnect().setValue(false)
        db.child("lastSeen").onDisconnect().setValue(System.currentTimeMillis())
    }

    fun setUserOffline(userId: String) {
        val db = FirebaseDatabase.getInstance().getReference("usersStatus").child(userId)

        db.child("online").setValue(false)
        db.child("lastSeen").setValue(System.currentTimeMillis())
    }
}