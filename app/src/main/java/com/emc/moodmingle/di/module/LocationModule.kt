package com.emc.moodmingle.di.module

import android.content.Context
import com.emc.moodmingle.api.nominatim.LocationProvider
import com.emc.moodmingle.api.nominatim.NominatimApi
import com.emc.moodmingle.di.module.qualifier.LocationRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    @LocationRetrofit
    fun provideMapRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNominatimApi(@LocationRetrofit retrofit: Retrofit): NominatimApi =
        retrofit.create(NominatimApi::class.java)

    @Provides
    @Singleton
    fun provideLocationProvider(
        @ApplicationContext context: Context
    ): LocationProvider = LocationProvider(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
//            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "MoodMingle/1.0 (emc.moodmingle@gmail.com)")
                        .build()
                )
            }
            .build()
}
