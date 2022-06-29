package com.project.vinance.view.recycler

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.database.TradeHistoryDatabase
import com.project.vinance.database.TradeHistoryTable
import com.project.vinance.view.GlobalData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RecycleTradeHistory(private val context: Context, var historyList: List<TradeHistoryTable>) : RecyclerView.Adapter<RecycleTradeHistory.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val import: RadioGroup = itemView.findViewById(R.id.trade_history_import_group)
        val importBuy: RadioButton = itemView.findViewById(R.id.trade_history_import_buy)
        val importSell: RadioButton = itemView.findViewById(R.id.trade_history_import_sell)
        val type: RadioGroup = itemView.findViewById(R.id.trade_history_type_group)
        val typeBuy: RadioButton = itemView.findViewById(R.id.trade_history_type_buy)
        val typeSell: RadioButton = itemView.findViewById(R.id.trade_history_type_sell)

        val coinResult: TextView = itemView.findViewById(R.id.trade_history_coin_result)
        val coin: EditText = itemView.findViewById(R.id.trade_history_coin_edit)
        val datetime: EditText = itemView.findViewById(R.id.trade_history_datetime_edit)
        val entryPrice: EditText = itemView.findViewById(R.id.trade_history_entry_price_edit)
        val sellPrice: EditText = itemView.findViewById(R.id.trade_history_sell_price_edit)
        val filled: EditText = itemView.findViewById(R.id.trade_history_filled_edit)
        val fee: TextView = itemView.findViewById(R.id.trade_history_fee_value)
        val pnl: TextView = itemView.findViewById(R.id.trade_history_realized_pnl_value)

        val search: Button = itemView.findViewById(R.id.trade_history_coin_button)
        val calculate: Button = itemView.findViewById(R.id.trade_history_calculate_button)
        val delete: Button = itemView.findViewById(R.id.trade_history_delete_button)

        fun bind(history: TradeHistoryTable, importId: Int, typeId: Int) {
            history.let {
                import.check(importId)
                coinResult.text = it.printCoin
                coin.setText(it.coin)
                type.check(typeId)
                datetime.setText(it.datetime)
                sellPrice.setText(it.sellPrice)
                entryPrice.setText(it.entryPrice)
                filled.setText(it.filled)
                fee.text = it.fee
                pnl.text = it.realizedPnl
            }
        }
    }

    fun update(list: List<TradeHistoryTable>) {
        historyList = list
        notifyDataSetChanged()
    }

    fun canNext(): Boolean {
        historyList.forEach {
            println("CHECKED $it")
            // 저장될 데이터 값에 대해
            if (it.id == null) {
                if (it.printCoin.isBlank() or it.fee.isBlank()) {
                    return false
                }

                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    dateFormat.isLenient = false
                    dateFormat.parse(it.datetime.split(" ")[0])
                } catch (e: Exception) {
                    return false
                }
            }
        }

        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trade_history, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        val importId: Int = if (item.whenSellBuy == "살 때") holder.importBuy.id else if (item.whenSellBuy == "팔 때") holder.importSell.id else 0
        val typeId: Int = if (item.type == "매수") holder.typeBuy.id else if (item.type == "매도") holder.typeSell.id else 0


        with(holder) {
            // 데이터베이스에서 불러온 값은 변경 불가능
            if (item.id != null) {
                importBuy.isEnabled = false
                importSell.isEnabled = false
                typeBuy.isEnabled = false
                typeSell.isEnabled = false
                coin.isEnabled = false
                search.isEnabled = false
                datetime.isEnabled = false
                entryPrice.isEnabled = false
                sellPrice.isEnabled = false
                filled.isEnabled = false
                calculate.isEnabled = false
            } else {
                importBuy.isEnabled = true
                importSell.isEnabled = true
                typeBuy.isEnabled = true
                typeSell.isEnabled = true
                coin.isEnabled = true
                search.isEnabled = true
                datetime.isEnabled = true
                entryPrice.isEnabled = true
                sellPrice.isEnabled = true
                filled.isEnabled = true
                calculate.isEnabled = true
            }



            import.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    importSell.id -> {
                        item.whenSellBuy = "팔 때"
                        sellPrice.isEnabled = true
                    }
                    importBuy.id -> {
                        item.whenSellBuy = "살 때"
                        sellPrice.isEnabled = false
                    }
                    else -> {
                        item.whenSellBuy = ""
                        sellPrice.isEnabled = false
                    }
                }
            }
            type.setOnCheckedChangeListener { _, checkedId ->
                item.type = if (checkedId == typeBuy.id) "매수" else if (checkedId == typeSell.id) "매도" else ""
            }
            coin.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.coin = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })
            datetime.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.datetime = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })
            entryPrice.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.entryPrice = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })
            sellPrice.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.sellPrice = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })
            filled.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.filled = s?.toString() ?: ""
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })

            // 검색 클릭
            search.setOnClickListener {
                val coinList = GlobalData.coinList
                val requestText = coin.text.toString()

                // 찾을 경우 출력
                coinList.find { (it == requestText.uppercase()) or ((it.substring(0, it.length - 4) == requestText.uppercase())) }?.let {
                    item.printCoin = it
                    coinResult.text = it
                }
            }

            // 계산하기 클릭
            calculate.setOnClickListener {
                try {
                    val entry = BigDecimal(item.entryPrice)
                    val size = BigDecimal(item.filled)
                    var commission: BigDecimal
                    var pnl: BigDecimal = BigDecimal.ZERO
                    when (item.whenSellBuy) {
                        "살 때" -> {
                            commission = (entry * size * BigDecimal("0.0002"))
                        }
                        "팔 때" -> {
                            val sell = BigDecimal(item.sellPrice)
                            commission = (sell * size * BigDecimal("0.0004"))
                            pnl = (sell * size * (BigDecimal.ONE - (entry.divide(sell, MathContext.DECIMAL128)))).setScale(8, RoundingMode.HALF_UP)
                            if (item.type == "매수") pnl *= BigDecimal("-1")
                        }
                        else -> {
                            return@setOnClickListener
                        }
                    }

                    item.fee = commission.toPlainString()
                    item.realizedPnl = pnl.toPlainString()

                    notifyDataSetChanged()
                } catch (e: Exception) { e.printStackTrace() }
            }

            // 삭제 클릭
            delete.setOnClickListener {
                // 데이터베이스에 저장된 데이터일 경우
                if (item.id != null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        TradeHistoryDatabase.getInstance(context)?.dao?.deleteHistory(item)
                    }
                }

                val newHistory = historyList.toMutableList()
                newHistory.removeAt(holder.adapterPosition)

                update(newHistory)
            }

            bind(item, importId, typeId)
        }
    }

    override fun getItemCount(): Int = historyList.size

    companion object {
        private val TAG = RecycleTradeHistory::class.java.simpleName
    }
}