package com.punyo.slatemap.application.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.net.PlacesClient
import com.punyo.slatemap.application.db.DatabaseProvider
import com.punyo.slatemap.data.location.source.UserLocationSource
import com.punyo.slatemap.data.poi.source.PoiInfoSource
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
    fun provideUserLocationSource(fusedLocationProviderClient: FusedLocationProviderClient): UserLocationSource =
        UserLocationSource(fusedLocationProviderClient)

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
    fun providePoiInfoSource(placesClient: PlacesClient): PoiInfoSource = PoiInfoSource(placesClient)

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext applicationContext: Context,
    ): Context = applicationContext
}
