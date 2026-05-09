package co.gorkem.namaz.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import co.gorkem.namaz.domain.model.LocationInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocoderHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getLocationInfo(
        latitude: Double,
        longitude: Double
    ): LocationInfo = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale("tr"))
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                LocationInfo(
                    latitude = latitude,
                    longitude = longitude,
                    timezone = TimeZone.getDefault().id,
                    cityName = address.adminArea ?: address.locality ?: "",
                    districtName = address.subAdminArea ?: address.locality ?: ""
                )
            } else {
                LocationInfo.DEFAULT.copy(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        } catch (e: Exception) {
            LocationInfo.DEFAULT.copy(
                latitude = latitude,
                longitude = longitude
            )
        }
    }
}
