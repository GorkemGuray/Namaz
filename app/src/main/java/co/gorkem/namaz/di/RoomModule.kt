package co.gorkem.namaz.di

import android.content.Context
import androidx.room.Room
import co.gorkem.namaz.data.local.room.NamazDatabase
import co.gorkem.namaz.data.local.room.PrayerTimeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideNamazDatabase(@ApplicationContext context: Context): NamazDatabase {
        return Room.databaseBuilder(
            context,
            NamazDatabase::class.java,
            "namaz_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun providePrayerTimeDao(database: NamazDatabase): PrayerTimeDao {
        return database.prayerTimeDao()
    }

    @Provides
    @Singleton
    fun provideZikirDao(database: NamazDatabase): co.gorkem.namaz.data.local.room.ZikirDao {
        return database.zikirDao()
    }
}
