package com.emc.moodmingle.di.module

import com.emc.moodmingle.api.soundcloud.SoundCloudApi
import com.emc.moodmingle.di.module.qualifier.SoundCloudRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SoundCloudModule {
    @Provides
    @Singleton
    @SoundCloudRetrofit
    fun provideSoundCloudRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://soundcloudbackend.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideSoundCloudApi(@SoundCloudRetrofit retrofit: Retrofit): SoundCloudApi =
        retrofit.create(SoundCloudApi::class.java)
}