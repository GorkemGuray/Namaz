package co.gorkem.namaz.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrayerTimeDao {

    @Query("SELECT * FROM prayer_times WHERE cityName = :cityName AND cacheMonth = :month AND cacheYear = :year AND cacheMethod = :method AND cacheSchool = :school ORDER BY date ASC")
    suspend fun getPrayerTimesForMonth(cityName: String, month: Int, year: Int, method: Int, school: Int): List<PrayerTimeEntity>

    @Query("SELECT * FROM prayer_times WHERE cityName = :cityName AND date = :date LIMIT 1")
    suspend fun getPrayerTimeForDate(cityName: String, date: String): PrayerTimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prayerTimes: List<PrayerTimeEntity>)

    @Query("DELETE FROM prayer_times WHERE cachedAt < :olderThanTimestamp")
    suspend fun deleteOlderThan(olderThanTimestamp: Long)

    @Query("DELETE FROM prayer_times WHERE cityName = :cityName AND cacheMonth = :month AND cacheYear = :year AND cacheMethod = :method AND cacheSchool = :school")
    suspend fun deleteMonthForCity(cityName: String, month: Int, year: Int, method: Int, school: Int)
}
