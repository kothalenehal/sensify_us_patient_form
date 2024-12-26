package com.sensifyawareapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ScentAware::class, AudioAware::class, TraceAware::class, GlanceAware::class,WordsAware::class,GrammarAware::class,Reminder::class,AlternateData::class,LocationData::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun awareDao(): AwaresDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, AppDatabase::class.java,
                    "sensify_aware_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            return instance!!
        }
    }
}
//object DatabaseBuilder {
//    private var INSTANCE: AppDatabase? = null
//    fun getInstance(context: Context): AppDatabase {
//        if (INSTANCE == null) {
//            synchronized(AppDatabase::class) {
//                INSTANCE = buildRoomDB(context)
//            }
//        }
//        return INSTANCE!!
//    }
//
//    private fun buildRoomDB(context: Context) =
//        Room.databaseBuilder(
//            context.applicationContext,
//            AppDatabase::class.java,
//            "SensifyAwareDB"
//        ).build()
//}

//class DatabaseHelperImpl(private val gfgDatabase: AppDatabase) : DatabaseHelper {
//    override suspend fun getCourses(): List<AudioAware> = gfgDatabase.awareDao().getAllAudioAware()
//    override suspend fun insertAll(Courses: List<Course>) =
//        gfgDatabase.CourseDao().insertAll(Courses)
//}
