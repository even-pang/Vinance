package com.project.vinance.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TradeHistoryDao {
    @Query("SELECT * FROM trade_history")
    fun getAllList(): List<TradeHistoryTable>

    @Insert
    fun insertHistory(history: TradeHistoryTable)

    @Delete
    fun deleteHistory(history: TradeHistoryTable)
}