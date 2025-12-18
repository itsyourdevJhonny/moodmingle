package com.emc.moodmingle.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.dao.favorites.FavoritesDao
import com.emc.moodmingle.data.dao.hide.HideDao
import com.emc.moodmingle.data.dao.post.CommentDao
import com.emc.moodmingle.data.dao.post.PostDao
import com.emc.moodmingle.data.dao.post.ReactionDao
import com.emc.moodmingle.data.dao.saved.SaveDao
import com.emc.moodmingle.data.dao.search.SearchDao
import com.emc.moodmingle.data.dao.share.ShareDao
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.data.model.favorites.FavoritesEntity
import com.emc.moodmingle.data.model.hide.HideEntity
import com.emc.moodmingle.data.model.post.CommentEntity
import com.emc.moodmingle.data.model.post.PostEntity
import com.emc.moodmingle.data.model.post.ReactionEntity
import com.emc.moodmingle.data.model.save.SaveEntity
import com.emc.moodmingle.data.model.search.SearchEntity
import com.emc.moodmingle.data.model.share.ShareEntity

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