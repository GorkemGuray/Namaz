package co.gorkem.namaz.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QadaPrayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QadaPrayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<QadaPrayerEntity>)

    @Update
    suspend fun update(entity: QadaPrayerEntity)

    @Delete
    suspend fun delete(entity: QadaPrayerEntity)

    @Query("SELECT * FROM qada_prayers WHERE id = :id")
    suspend fun getById(id: Long): QadaPrayerEntity?

    @Query("SELECT * FROM qada_prayers ORDER BY dateMillis DESC, id DESC")
    fun getAll(): Flow<List<QadaPrayerEntity>>

    @Query("SELECT * FROM qada_prayers WHERE prayerName = :prayerName ORDER BY dateMillis DESC, id DESC")
    fun getByPrayer(prayerName: String): Flow<List<QadaPrayerEntity>>

    @Query("SELECT COUNT(*) FROM qada_prayers WHERE prayerName = :prayerName AND isDone = 0")
    fun countByPrayer(prayerName: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM qada_prayers WHERE prayerName = :prayerName AND isDone = 0")
    suspend fun getCountByPrayer(prayerName: String): Int

    @Query("SELECT * FROM qada_prayers WHERE prayerName = :prayerName AND isDone = 0 ORDER BY dateMillis ASC, id ASC LIMIT 1")
    suspend fun getOldestUndone(prayerName: String): QadaPrayerEntity?

    @Query("SELECT * FROM qada_prayers WHERE prayerName = :prayerName AND isDone = 1 ORDER BY dateMillis DESC, id DESC LIMIT 1")
    suspend fun getLatestDone(prayerName: String): QadaPrayerEntity?

    @Query("DELETE FROM qada_prayers WHERE prayerName = :prayerName")
    suspend fun deleteAllByPrayer(prayerName: String)
}
