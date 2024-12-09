package com.my.dailycashflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CashFlow::class, Category::class], version = 1)
abstract class CashFlowDatabase : RoomDatabase() {

    abstract fun cashFlowDao(): CashFlowDao

    companion object {
        @Volatile
        private var instance: CashFlowDatabase? = null

        fun getInstance(context: Context): CashFlowDatabase {
            return instance ?: synchronized(CashFlowDatabase::class.java) {
                val INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CashFlowDatabase::class.java,
                    "cashflow.db"
                ).createFromAsset("cashflow.db")
                    .build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }
}