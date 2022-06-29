package com.project.vinance.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_history")
data class TradeHistoryTable(
    var whenSellBuy: String,
    var printCoin: String,
    var coin: String,
    var type: String,
    var datetime: String,
    var entryPrice: String,
    var sellPrice: String,
    var filled: String,
    var fee: String,
    var realizedPnl: String,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) {
    companion object {
        val EMPTY: TradeHistoryTable
            get() = TradeHistoryTable("", "", "", "", "", "", "", "", "", "")
    }
}