package co.gorkem.namaz.di

import co.gorkem.namaz.data.local.datastore.UserPreferencesRepositoryImpl
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.data.repository.ZikirRepositoryImpl
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import co.gorkem.namaz.domain.repository.UserPreferencesRepository
import co.gorkem.namaz.domain.repository.ZikirRepository
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
    abstract fun bindPrayerTimeRepository(
        impl: PrayerTimeRepositoryImpl
    ): PrayerTimeRepository

    @Binds
    @Singleton
    abstract fun bindZikirRepository(
        impl: ZikirRepositoryImpl
    ): ZikirRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
