package com.emc.moodmingle.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emc.moodmingle.domain.local.dao.user.UserDao
import com.emc.moodmingle.domain.local.dao.favorites.FavoritesDao
import com.emc.moodmingle.domain.local.dao.hide.HideDao
import com.emc.moodmingle.domain.local.dao.post.CommentDao
import com.emc.moodmingle.domain.local.dao.post.PostDao
import com.emc.moodmingle.domain.local.dao.post.ReactionDao
import com.emc.moodmingle.domain.local.dao.saved.SaveDao
import com.emc.moodmingle.domain.local.dao.search.SearchDao
import com.emc.moodmingle.domain.local.dao.share.ShareDao
import com.emc.moodmingle.domain.local.model.user.UserEntity
import com.emc.moodmingle.domain.local.model.favorites.FavoritesEntity
import com.emc.moodmingle.domain.local.model.hide.HideEntity
import com.emc.moodmingle.domain.local.model.post.CommentEntity
import com.emc.moodmingle.domain.local.model.post.PostEntity
import com.emc.moodmingle.domain.local.model.post.ReactionEntity
import com.emc.moodmingle.domain.local.model.save.SaveEntity
import com.emc.moodmingle.domain.local.model.search.SearchEntity
import com.emc.moodmingle.domain.local.model.share.ShareEntity

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        ReactionEntity::class,
        SaveEntity::class,
        FavoritesEntity::class,
        ShareEntity::class,
        SearchEntity::class,
        HideEntity::class
    ],
    version = 20,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun reactionDao(): ReactionDao
    abstract fun saveDao(): SaveDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun shareDao(): ShareDao
    abstract fun searchDao(): SearchDao
    abstract fun hideDao(): HideDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moodmingle_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}