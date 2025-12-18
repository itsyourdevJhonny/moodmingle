package com.emc.moodmingle.di

import com.emc.moodmingle.data.firebase.datasource.FirebaseUserDataSource
import com.emc.moodmingle.data.firebase.repository.CommentRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.favorites.FavoritesRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.HideRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.ReactionRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.saved.SaveRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.ShareRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.UserRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.chat.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseUserDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FirebaseUserDataSource = FirebaseUserDataSource(firestore, auth)

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseUserDataSource: FirebaseUserDataSource
    ): UserRepositoryFirebase = UserRepositoryFirebase(firebaseUserDataSource)

    @Provides
    @Singleton
    fun provideCommentRepository(firestore: FirebaseFirestore): CommentRepositoryFirebase =
        CommentRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideReactionRepository(firestore: FirebaseFirestore): ReactionRepositoryFirebase =
        ReactionRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideShareRepository(firestore: FirebaseFirestore): ShareRepositoryFirebase =
        ShareRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideSaveRepository(firestore: FirebaseFirestore): SaveRepositoryFirebase =
        SaveRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideHideRepository(firestore: FirebaseFirestore): HideRepositoryFirebase =
        HideRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideFavoritesRepository(firestore: FirebaseFirestore): FavoritesRepositoryFirebase =
        FavoritesRepositoryFirebase(firestore)

    @Provides
    @Singleton
    fun provideChatRepository(firestore: FirebaseFirestore): ChatRepository =
        ChatRepository(firestore)
}
