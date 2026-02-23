package com.emc.moodmingle.di.module

import com.emc.moodmingle.domain.local.dao.favorites.FavoritesDao
import com.emc.moodmingle.domain.local.dao.post.CommentDao
import com.emc.moodmingle.domain.local.dao.post.PostDao
import com.emc.moodmingle.domain.local.dao.post.ReactionDao
import com.emc.moodmingle.domain.local.dao.saved.SaveDao
import com.emc.moodmingle.domain.local.dao.search.SearchDao
import com.emc.moodmingle.domain.local.dao.share.ShareDao
import com.emc.moodmingle.domain.local.repository.post.CommentRepository
import com.emc.moodmingle.domain.local.repository.post.PostRepository
import com.emc.moodmingle.domain.local.service.CommentService
import com.emc.moodmingle.domain.local.service.FavoritesService
import com.emc.moodmingle.domain.local.service.PostService
import com.emc.moodmingle.domain.local.service.ReactionService
import com.emc.moodmingle.domain.local.service.SaveService
import com.emc.moodmingle.domain.local.service.SearchService
import com.emc.moodmingle.domain.local.service.ShareService
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