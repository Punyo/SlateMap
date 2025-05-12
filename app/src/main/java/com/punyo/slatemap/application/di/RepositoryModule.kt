package com.punyo.slatemap.application.di

import com.punyo.slatemap.data.location.LocationRepository
import com.punyo.slatemap.data.location.LocationRepositoryImpl
import com.punyo.slatemap.data.poi.PoiRepository
import com.punyo.slatemap.data.poi.PoiRepositoryImpl
import com.punyo.slatemap.data.unlockedlocality.UnlockedLocalityRepository
import com.punyo.slatemap.data.unlockedlocality.UnlockedLocalityRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindUnlockedLocalityRepository(unlockedLocalityRepositoryImpl: UnlockedLocalityRepositoryImpl): UnlockedLocalityRepository

    @Binds
    @Singleton
    abstract fun bindPoiRepository(poiRepositoryImpl: PoiRepositoryImpl): PoiRepository
}
