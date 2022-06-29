package com.project.vinance.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TradeHistoryTable::class], version = 1)
abstract class TradeHistoryDatabase: RoomDatabase() {
    val dao: TradeHistoryDao get() = getTradeHistory()

    protected abstract fun getTradeHistory(): TradeHistoryDao

    companion object {
        private var instance: TradeHistoryDatabase? = null

        fun getInstance(context: Context): TradeHistoryDatabase? {
            if (instance == null) {
                synchronized(TradeHistoryDatabase::class) {
                    instance = Room.databaseBuilder(
                        context,
                        TradeHistoryDatabase::class.java,
                        "tradeHistory"
                    ).build()
                }
            }

            return instance
        }
    }
}