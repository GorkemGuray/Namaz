package co.gorkem.namaz.data.local.datastore

import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.getLocationInfo as domainGetLocationInfo
import co.gorkem.namaz.domain.model.activeDataSource as domainActiveDataSource

typealias RecentCity = co.gorkem.namaz.domain.model.RecentCity
typealias UserPreferences = co.gorkem.namaz.domain.model.UserPreferences
typealias UserPreferencesRepository = co.gorkem.namaz.domain.repository.UserPreferencesRepository

val UserPreferences.activeDataSource: Int
    get() = domainActiveDataSource

fun UserPreferences.getLocationInfo(): LocationInfo = domainGetLocationInfo()
