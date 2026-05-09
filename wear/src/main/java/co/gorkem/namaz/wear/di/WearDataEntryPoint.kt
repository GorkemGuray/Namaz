package co.gorkem.namaz.wear.di

import co.gorkem.namaz.wear.data.WearDataLayerService
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.data.WearPrayerRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WearDataEntryPoint {
    val dataLayerService: WearDataLayerService
    val prayerRepository: WearPrayerRepository
}
