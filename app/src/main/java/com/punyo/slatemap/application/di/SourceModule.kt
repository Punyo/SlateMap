package com.punyo.slatemap.application.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.punyo.slatemap.application.db.DatabaseProvider
import com.punyo.slatemap.data.location.source.UserLocationSource
import com.punyo.slatemap.data.unlockedlocality.source.UnlockedLocalitySource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceModule {
    @Provides
    @Singleton
    fun provideUserLocationSource(
        @ApplicationContext context: Context,
    ): UserLocationSource = UserLocationSource(LocationServices.getFusedLocationProviderClient(context))

    @Provides
    @Singleton
    fun provideUnlockedLocalitySource(
        @ApplicationContext context: Context,
    ): UnlockedLocalitySource =
        UnlockedLocalitySource(
            DatabaseProvider.getDatabase(context).unlockedLocalityDao(),
        )

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext applicationContext: Context,
    ): Context = applicationContext
}
