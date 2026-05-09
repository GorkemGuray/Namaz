package co.gorkem.namaz.data.local.datastore

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return defaultValue
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
    }
}
