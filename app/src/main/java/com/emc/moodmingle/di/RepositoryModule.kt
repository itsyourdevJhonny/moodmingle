package com.emc.moodmingle.di

import com.emc.moodmingle.data.dao.favorites.FavoritesDao
import com.emc.moodmingle.data.dao.post.CommentDao
import com.emc.moodmingle.data.dao.post.PostDao
import com.emc.moodmingle.data.dao.post.ReactionDao
import com.emc.moodmingle.data.dao.saved.SaveDao
import com.emc.moodmingle.data.dao.search.SearchDao
import com.emc.moodmingle.data.dao.share.ShareDao
import com.emc.moodmingle.data.repository.post.CommentRepository
import com.emc.moodmingle.data.repository.post.PostRepository
import com.emc.moodmingle.data.service.CommentService
import com.emc.moodmingle.data.service.FavoritesService
import com.emc.moodmingle.data.service.PostService
import com.emc.moodmingle.data.service.ReactionService
import com.emc.moodmingle.data.service.SaveService
import com.emc.moodmingle.data.service.SearchService
import com.emc.moodmingle.data.service.ShareService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(postDao: PostDao): PostRepository =
        PostRepository(postDao)

    @Provides
    @Singleton
    fun provideCommentRepository(commentDao: CommentDao): CommentRepository =
        CommentRepository(commentDao)

    @Provides
    @Singleton
    fun providePostService(postRepository: PostRepository): PostService =
        PostService(postRepository)

    @Provides
    @Singleton
    fun provideCommentService(commentRepository: CommentRepository): CommentService =
        CommentService(commentRepository)

    @Provides
    @Singleton
    fun provideReactionService(reactionDao: ReactionDao): ReactionService =
        ReactionService(reactionDao)

    @Provides
    @Singleton
    fun provideSaveService(saveDao: SaveDao): SaveService = SaveService(saveDao)

    @Provides
    @Singleton
    fun provideFavoritesService(favoritesDao: FavoritesDao): FavoritesService =
        FavoritesService(favoritesDao)

    @Provides
    @Singleton
    fun provideShareService(shareDao: ShareDao): ShareService = ShareService(shareDao)

    @Provides
    @Singleton
    fun provideSearchService(searchDao: SearchDao): SearchService = SearchService(searchDao)
}