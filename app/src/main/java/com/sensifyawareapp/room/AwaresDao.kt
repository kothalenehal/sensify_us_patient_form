package com.sensifyawareapp.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AwaresDao {

    @Query("SELECT * FROM traceaware")
    fun getAllTraceAware(): Flow<List<TraceAware>>

    @Query("SELECT * FROM audioaware")
//    @Query("SELECT * FROM audioaware WHERE level LIKE :level")
    fun getAudioAwareByLevel(): Flow<List<AudioAware>>

    @Query("SELECT * FROM scentaware WHERE kit_size LIKE :kit")
    fun getScentAwareByKit(kit: Int): Flow<List<ScentAware>>

    @Query("SELECT * FROM glanceaware")
    fun getGlanceAware(): Flow<List<GlanceAware>>

    @Query("SELECT * FROM wordsaware")
    fun getWordsAware(): Flow<List<WordsAware>>
    @Query("SELECT * FROM grammaraware")
    fun getGrammarAware(): Flow<List<GrammarAware>>
    @Query("SELECT * FROM reminder")
    fun getReminder(): Flow<List<Reminder>>

    @Query("SELECT * FROM alternatedata where location_id = :languageCode")
    fun getAlternateData(languageCode: Int): Flow<List<AlternateData>>

    @Query("SELECT * FROM locationdata where language_name = :languageId")
    fun getLocationData(languageId: String): Flow<List<LocationData>>

    @Query("DELETE FROM alternatedata")
    fun deleteAll()


    @Insert
    suspend fun insertScentAware(scentAware: ScentAware)

    @Insert
    suspend fun insertAudioAware(audioAware: AudioAware)

    @Insert
    suspend fun insertTraceAware(traceAware: TraceAware)

    @Insert
    suspend fun insertGlanceAware(glanceAware: GlanceAware)

    @Insert
    suspend fun insertWordsAware(wordsAware: WordsAware)

    @Insert
    suspend fun insertGrammarAware(grammarAware: GrammarAware)

    @Insert
    suspend fun insertReminder(reminder:  Reminder)

    @Insert
    suspend fun insertReminderId(entity: Reminder): Long

    @Insert
    suspend fun insertAlternateData(alternateData:  AlternateData)

    @Update
    suspend fun updateAlternateData(alternateData: AlternateData)

    @Insert
    suspend fun insertLocationData(locationData:  LocationData)

    @Update
    suspend fun updateLocationData(locationData: LocationData)

    @Query("SELECT end_date FROM scentaware where kit_size = :kitSize order by id desc limit 1")
    fun getScentAwareLastDate(kitSize: Int): Flow<List<ScentAware>>

    @Query("SELECT end_date FROM traceaware order by id desc limit 1")
    fun getTraceAwareLastDate(): Flow<List<TraceAware>>

    @Query("SELECT end_date FROM audioaware order by id desc limit 1")
    fun getAudioAwareLastDate(): Flow<List<AudioAware>>

    @Query("SELECT end_date FROM glanceaware order by id desc limit 1")
    fun getGlanceAwareLastDate(): Flow<List<GlanceAware>>

    @Query("SELECT end_date FROM wordsaware order by id desc limit 1")
    fun getWordsAwareLastDate(): Flow<List<WordsAware>>

    @Query("SELECT end_date FROM grammaraware order by id desc limit 1")
    fun getGrammarAwareLastDate(): Flow<List<GrammarAware>>

    @Delete
    fun deleteReminder(friendHistoryData: Reminder)

}