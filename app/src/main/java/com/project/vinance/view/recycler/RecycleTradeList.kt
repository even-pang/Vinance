package com.project.vinance.view.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.database.TradeHistoryTable
import java.math.BigDecimal

class RecycleTradeList(private val context: Context, private val tradeList: List<TradeHistoryTable>): RecyclerView.Adapter<RecycleTradeList.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.recycler_future_trade_title)
        val type: TextView = itemView.findViewById(R.id.recycler_future_trade_long_short)
        val date: TextView = itemView.findViewById(R.id.recycler_future_trade_date)

        val price: TextView = itemView.findViewById(R.id.recycler_future_trade_price_value)
        val filled: TextView = itemView.findViewById(R.id.recycler_future_trade_conclusion_value)
        val fee: TextView = itemView.findViewById(R.id.recycler_future_trade_fee_value)
        val pnl: TextView = itemView.findViewById(R.id.recycler_future_trade_pnl_value)

        fun bind(trade: TradeHistoryTable, typeColor: Int) {
            title.text = trade.printCoin + " 무기한"
            type.text = trade.type
            date.text = trade.datetime

            price.text = trade.entryPrice
            filled.text = trade.filled
            fee.text = trade.fee
            pnl.text = BigDecimal(trade.realizedPnl).setScale(8).toPlainString()

            type.setTextColor(typeColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_future_trade, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(tradeList[position]) {
            val color = if(type == "매수") {
                ContextCompat.getColor(context, R.color.buy_color)
            } else {
                ContextCompat.getColor(context, R.color.sell_color)
            }

            holder.bind(this, color)
        }
    }

    override fun getItemCount(): Int = tradeList.size
}