package co.gorkem.namaz.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ZikirDao {
    @Query("SELECT * FROM zikirs ORDER BY id ASC")
    fun getAllZikirs(): Flow<List<ZikirEntity>>

    @Query("SELECT * FROM zikirs WHERE isNamazTesbihati = 1 LIMIT 1")
    fun getNamazTesbihati(): Flow<ZikirEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZikir(zikir: ZikirEntity)

    @Update
    suspend fun updateZikir(zikir: ZikirEntity)

    @Delete
    suspend fun deleteZikir(zikir: ZikirEntity)

    @Query("UPDATE zikirs SET currentCount = :count, currentPhase = :phase WHERE id = :id")
    suspend fun updateCountAndPhase(id: Int, count: Int, phase: Int)
}
