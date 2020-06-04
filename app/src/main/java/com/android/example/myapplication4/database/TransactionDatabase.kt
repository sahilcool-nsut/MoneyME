package com.android.example.myapplication4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = true)
abstract class TransactionDatabase:RoomDatabase(){
    abstract val transactionDatabaseDao: TransactionDatabasedao
    companion object {

        @Volatile
        private var INSTANCE: TransactionDatabase? = null
        fun getInstance(context: Context): TransactionDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionDatabase::class.java,
                        "transaction_history_database")

                        //.fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }
}