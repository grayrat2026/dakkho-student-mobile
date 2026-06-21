package com.dakkho.android.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dakkho.android.data.db.entity.AppSettingsEntity

@Dao
interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun get(key: String): AppSettingsEntity?

    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getValue(key: String): String?

    @Query("SELECT * FROM app_settings")
    suspend fun getAll(): List<AppSettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: AppSettingsEntity)

    @Query("INSERT OR REPLACE INTO app_settings (`key`, value) VALUES (:key, :value)")
    suspend fun setValue(key: String, value: String)

    @Query("DELETE FROM app_settings WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM app_settings")
    suspend fun deleteAll()
}
