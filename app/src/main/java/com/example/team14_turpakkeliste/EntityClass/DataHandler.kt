package com.example.team14_turpakkeliste.EntityClass


import androidx.room.ColumnInfo;
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "pakklister")
data class Pakkliste (
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "First_name") val firstName: String?,
    @ColumnInfo(name= "last_name") val lastName: String?
    )
data class ClothingObject(
    @ColumnInfo(name = "material") val material: String?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "layer") val layer: String?,
    @ColumnInfo(name = "warmth") val warmth: Int?,
    @ColumnInfo(name = "windproof") val windproof: Int?,
    @ColumnInfo(name = "waterproof") val waterproof: Int?,
    @ColumnInfo(name = "imageCode") val image: String?
)
data class WeatherInfo(
    @ColumnInfo(name = "temperature") val temperature: Double?,
    @ColumnInfo(name = "windspeed") val windspeed: Double?,
    @ColumnInfo(name = "watermilimeter") val watermilimeter: Double?,
    @ColumnInfo(name = "imageCode") val image: String?
)
@Dao
interface UserDao {
    @Query("SELECT * FROM pakklister")
    fun getAll(): List<Pakkliste>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Pakkliste)

    @Delete
    suspend fun delete(user: Pakkliste)

    @Query("DELETE FROM pakklister")
    fun deleteAll()

}