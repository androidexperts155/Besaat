package com.deepak.besaat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.deepak.besaat.dao.StoresDao
import com.deepak.besaat.model.ModelSourceDeatils.SourceDetails
import com.deepak.besaat.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [SourceDetails::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storesDao(): StoresDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context
//                        ,scope: CoroutineScope
        ): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.APP_DB_NAME
                )
//                    .addCallback(AppDatabaseCallback(scope))   // for callBack at firstTime database open
//                    .fallbackToDestructiveMigration()   // to avoid migration data version control-> It will clear all previous version data if version changed.----------> more info---(https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929)
                    //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // OR  to add alteration in version 2 or 3 like change in schema, Then don't use above commented line
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since we didn't alter the table, there's nothing else to do here.
//            database.execSQL("ALTER TABLE users "
//                    + " ADD COLUMN last_update INTEGER")
            }
        }
    }

    // to insert data at frst time by default//
//    private class AppDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    var storesDao = database.storesDao()
//
//                    // Delete all content here.
////                    storesDao.deleteAll()
//
//                    // Add sample
//                    var data = SourceDetails()
//                    storesDao.insertAll(data)
//
//                }
//            }
//        }
//    }
}