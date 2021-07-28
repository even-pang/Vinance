package com.project.vinance.view.recycler

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.databinding.RecycleFuturePositionBinding
import com.project.vinance.network.socket.SimpleSocketClient
import com.project.vinance.view.FutureData
import com.project.vinance.view.sub.enumerate.ADL
import com.project.vinance.view.sub.InputDataDTO
import com.project.vinance.view.sub.enumerate.LongShort
import java.math.BigDecimal
import java.math.RoundingMode

class RecycleFuturePosition(private val context: Context, private val pager: RecyclerView?) :
    RecyclerView.Adapter<RecycleFuturePosition.ViewHolder>() {
    companion object {
        var onceValue: Int? = 0

        private val TAG = RecycleFuturePosition::class.java.simpleName
    }

    private lateinit var binding: RecycleFuturePositionBinding

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: InputDataDTO, res: Int, adl: List<Int>) {
            val coinName = data.coinName.first
            val quantityRound = data.roundingQuantity
            val priceRound = data.roundingPrice

            val roe = itemView.findViewById<TextView>(R.id.recycle_position_roe_value)
            val pnl = itemView.findViewById<TextView>(R.id.recycle_position_pnl_value)
            if (onceValue != null) {
                if (onceValue!! > 0) {
                    roe.setTextColor(ContextCompat.getColor(context, R.color.buy_color))
                    pnl.setTextColor(ContextCompat.getColor(context, R.color.buy_color))
                } else {
                    roe.setTextColor(ContextCompat.getColor(context, R.color.sell_color))
                    pnl.setTextColor(ContextCompat.getColor(context, R.color.sell_color))
                }
                onceValue = null
            }

            itemView.findViewById<ImageView>(R.id.recycle_position_symbol_icon).setImageResource(res)
            itemView.findViewById<TextView>(R.id.recycle_position_size_coin).text = coinName.substring(0..coinName.length - 5)
            itemView.findViewById<TextView>(R.id.recycle_position_symbol_title).text = String.format("%s 무기한", coinName)
            itemView.findViewById<TextView>(R.id.recycle_position_mode_scale).text = String.format("%sx", data.leverage)
            pnl.text = data.pnl.setScale(2, RoundingMode.HALF_UP).toPlainString()
            roe.text = String.format("%s%%", data.roe.setScale(2, RoundingMode.HALF_UP))
            itemView.findViewById<TextView>(R.id.recycle_position_size_value).text =
                BigDecimal(data.size).setScale(quantityRound, RoundingMode.HALF_UP).toPlainString()
            itemView.findViewById<TextView>(R.id.recycle_position_danger_value).text = String.format("%s%%", data.danger.setScale(2, RoundingMode.HALF_UP))
            itemView.findViewById<TextView>(R.id.recycle_position_margin_value).text = data.margin.setScale(2, RoundingMode.HALF_UP).toPlainString()
            itemView.findViewById<TextView>(R.id.recycle_position_entry_price_value).text =
                BigDecimal(data.entryPrice).setScale(priceRound, RoundingMode.HALF_UP).toPlainString()
            itemView.findViewById<TextView>(R.id.recycle_position_market_price_value).text =
                data.marketPrice.setScale(priceRound, RoundingMode.HALF_UP).toPlainString()
            itemView.findViewById<TextView>(R.id.recycle_position_liquidation_value).text =
                data.liquidationPrice.setScale(priceRound, RoundingMode.HALF_UP).toPlainString()
            itemView.findViewById<TextView>(R.id.recycle_position_market_price_value).text =
                data.marketPrice.setScale(priceRound, RoundingMode.HALF_UP).toPlainString()


            itemView.findViewById<ImageView>(R.id.recycle_position_display1).imageTintList = ColorStateList.valueOf(adl[0])
            itemView.findViewById<ImageView>(R.id.recycle_position_display2).imageTintList = ColorStateList.valueOf(adl[1])
            itemView.findViewById<ImageView>(R.id.recycle_position_display3).imageTintList = ColorStateList.valueOf(adl[2])
            itemView.findViewById<ImageView>(R.id.recycle_position_display4).imageTintList = ColorStateList.valueOf(adl[3])
            /*binding.recyclePositionSymbolIcon.setImageResource(res)
            binding.recyclePositionSizeCoin.text = coinName.substring(0..coinName.length - 5)
            binding.recyclePositionSymbolTitle.text = String.format("%s 무기한", coinName)
            binding.recyclePositionModeScale.text = String.format("%sx", data.leverage)
            binding.recyclePositionPnlValue.text =
                String.format("%s", data.pnl.setScale(4, RoundingMode.HALF_UP).toPlainString())
            binding.recyclePositionRoeValue.text = String.format("%s%%", data.roe.setScale(2, RoundingMode.HALF_UP))
            binding.recyclePositionSizeValue.text = BigDecimal(data.size).setScale(quantityRound, RoundingMode.HALF_UP).toPlainString()
            binding.recyclePositionDangerValue.text = String.format("%s%%", data.danger.setScale(2, RoundingMode.HALF_UP))
            binding.recyclePositionMarginValue.text = data.margin.setScale(2, RoundingMode.HALF_UP).toPlainString()
            binding.recyclePositionEntryPriceValue.text = BigDecimal(data.entryPrice).setScale(priceRound, RoundingMode.HALF_UP).toPlainString()
            binding.recyclePositionMarketPriceValue.text =
                String.format("%s", data.marketPrice.setScale(priceRound, RoundingMode.HALF_UP).toPlainString())
            binding.recyclePositionLiquidationValue.text =
                String.format("%s", data.liquidationPrice.setScale(priceRound, RoundingMode.HALF_UP).toPlainString()) */
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        binding = RecycleFuturePositionBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.recycle_future_position, parent, false))
        binding = RecycleFuturePositionBinding.inflate(LayoutInflater.from(parent.context))


        SimpleSocketClient.futurePosition = this
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_future_position, parent, false))
//        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = FutureData.inputDataList[position]

        val res: Int = if (data.longShort == LongShort.Long) {
            R.drawable.ic_long
        } else {
            R.drawable.ic_short
        }

        val grey = ContextCompat.getColor(context, R.color.deep_grey)
        val green = ContextCompat.getColor(context, R.color.buy_color)
        val red = ContextCompat.getColor(context, R.color.sell_color)
        val adl: List<Int> = when (data.adl) {
            ADL.ZERO -> listOf(grey, grey, grey, grey)
            ADL.ONE -> listOf(green, grey, grey, grey)
            ADL.TWO -> listOf(green, green, grey, grey)
            ADL.THREE -> listOf(green, green, green, grey)
            ADL.FOUR -> listOf(red, red, red, red)
        }

        holder.bind(data, res, adl)
    }

    /** 리사이클 개수  */
    override fun getItemCount(): Int {
        return FutureData.inputDataList.size
    }

    private val handler = Handler(Looper.getMainLooper())
    fun change() {
        handler.post {
            notifyDataSetChanged()
            pager?.invalidate()
        }
    }

    fun toGreenColor(pnl: TextView, roe: TextView, danger: TextView?) {
        handler.post {
            pnl.setTextColor(ContextCompat.getColor(context, R.color.buy_color))
            roe.setTextColor(ContextCompat.getColor(context, R.color.buy_color))
            danger?.setTextColor(ContextCompat.getColor(context, R.color.buy_color))
        }
    }

    fun toRedColor(pnl: TextView, roe: TextView, danger: TextView?) {
        handler.post {
            pnl.setTextColor(ContextCompat.getColor(context, R.color.sell_color))
            roe.setTextColor(ContextCompat.getColor(context, R.color.sell_color))
            danger?.setTextColor(ContextCompat.getColor(context, R.color.sell_color))
        }
    }
}