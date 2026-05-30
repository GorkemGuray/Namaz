package co.gorkem.namaz.di

import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
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
        impl: co.gorkem.namaz.data.repository.ZikirRepositoryImpl
    ): co.gorkem.namaz.domain.repository.ZikirRepository
}
