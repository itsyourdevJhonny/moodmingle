package com.emc.moodmingle.di.module

import android.content.Context
import androidx.room.Room
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.dao.favorites.FavoritesDao
import com.emc.moodmingle.data.dao.hide.HideDao
import com.emc.moodmingle.data.dao.post.CommentDao
import com.emc.moodmingle.data.dao.post.PostDao
import com.emc.moodmingle.data.dao.post.ReactionDao
import com.emc.moodmingle.data.dao.saved.SaveDao
import com.emc.moodmingle.data.dao.search.SearchDao
import com.emc.moodmingle.data.dao.share.ShareDao
import com.emc.moodmingle.di.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "moodmingle_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun provideCommentDao(db: AppDatabase): CommentDao = db.commentDao()

    @Provides
    fun provideReactionDao(db: AppDatabase): ReactionDao = db.reactionDao()

    @Provides
    fun provideSaveDao(db: AppDatabase): SaveDao = db.saveDao()

    @Provides
    fun provideFavoritesDao(db: AppDatabase): FavoritesDao = db.favoritesDao()

    @Provides
    fun provideShareDao(db: AppDatabase): ShareDao = db.shareDao()

    @Provides
    fun provideHideDao(db: AppDatabase): HideDao = db.hideDao()

    @Provides
    fun provideSearchDao(db: AppDatabase): SearchDao = db.searchDao()
}