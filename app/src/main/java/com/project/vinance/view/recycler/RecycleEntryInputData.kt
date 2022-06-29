package com.project.vinance.view.recycler

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.databinding.RecyclerInputDataBinding
import com.project.vinance.view.sub.InputDataDTO
import java.util.logging.Logger

class RecycleEntryInputData(private val coinList: List<String>, private val recyclerManager: RecyclerView.LayoutManager) :
    RecyclerView.Adapter<RecycleEntryInputData.ViewHolder>() {
    private val dataList: MutableList<InputDataDTO> = mutableListOf()
    private val TAG = RecycleEntryInputData::class.java.simpleName

    private lateinit var binding: RecyclerInputDataBinding
    private lateinit var context: Context

    inner class ViewHolder(private val binding: RecyclerInputDataBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.recyclerCoinTypeButton.setOnClickListener {
                val editText = binding.recyclerCoinTypeEdit.text
                Log.i("SPECIAL", binding.recyclerCoinTypeEdit.text.toString())

                Log.i("SPECIAL", coinList.toString());
                coinList.find { (it == editText.toString().uppercase()) or (it.substring(0, it.length - 4) == editText.toString().uppercase())
                }?.let { binding.recyclerSearchResultValue.text = it }
            }
        }

        fun bind(position: Int) {
            recyclerManager.findViewByPosition(position)?.findViewById<AppCompatButton>(R.id.recycler_coin_type_button)?.setOnClickListener {
                Log.d(TAG, it.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = RecyclerInputDataBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.recycler_input_data, parent, false))

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun addField() {
        dataList.add(InputDataDTO.createEmpty())
        Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged)
    }
}