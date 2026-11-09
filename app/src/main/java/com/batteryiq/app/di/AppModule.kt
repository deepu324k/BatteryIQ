package com.batteryiq.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.batteryiq.app.data.preferences.SettingsDataStore
import com.batteryiq.app.data.repository.BatteryRepository
import com.batteryiq.app.data.repository.SystemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("batteryiq_settings")
        }
    }

    @Provides
    @Singleton
    fun provideBatteryRepository(@ApplicationContext context: Context): BatteryRepository {
        return BatteryRepository(context)
    }

    @Provides
    @Singleton
    fun provideSystemRepository(@ApplicationContext context: Context): SystemRepository {
        return SystemRepository(context)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(dataStore: DataStore<Preferences>): SettingsDataStore {
        return SettingsDataStore(dataStore)
    }
}
