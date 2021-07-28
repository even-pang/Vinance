package com.project.vinance.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.project.vinance.R
import com.project.vinance.databinding.ActivityEntryBinding
import com.project.vinance.network.rest.BinanceRest
import com.project.vinance.network.rest.vo.ExchangeInfoDTO
import com.project.vinance.view.recycler.RecycleEntryInputData
import com.project.vinance.view.sub.InputDataDTO
import com.project.vinance.view.sub.enumerate.ADL
import com.project.vinance.view.sub.enumerate.LongShort
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.util.*

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var inputDataAdapter: RecycleEntryInputData
    private var coinDataList: MutableList<String> = mutableListOf()
    private val TAG = EntryActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        val year = 2021
        val month = 8
        val day = 5

        val maxUsable = Calendar.getInstance(Locale.getDefault())
        maxUsable.set(year, month - 1, day)

        val shutdown = maxUsable.timeInMillis
        val current = System.currentTimeMillis()

        // 실행한 날짜가 지정한 날짜를 지난 경우
        if (current >= shutdown) {
            Toast.makeText(this, "Can not open", Toast.LENGTH_SHORT).show()
            finish()
        }

        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }


    private fun init() {
        CoroutineScope(Dispatchers.Main).launch {
            val exchange = initNetwork()
            initSpinner(exchange)
            initAdapter()
            initButtons()
        }
    }

    /**
     * 초기 REST API 데이터 가져오기
     * */
    private suspend fun initNetwork() = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
        try {
            FutureData.exchangeInfo = GlobalData.getClient(GlobalData.FAPI_URL).getExchangeInfo().execute().body()
            val result = FutureData.exchangeInfo!!

            coinDataList = (listOf("선택하세요") + result.symbols.filter { (it.contractType == "PERPETUAL") and (it.quoteAsset == "USDT") }
                .map { it.symbol }) as MutableList<String>
            // 기존에 가져온 데이터에 필터 무기한과 USDT 값만 필터링하여 반환
            ExchangeInfoDTO(result.timezone, result.serverTime, result.futuresType, result.rateLimits, result.assets,
                result.symbols.filter { (it.contractType == "PERPETUAL") and (it.quoteAsset == "USDT") }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 표시 데이터 설정
     * */
    private fun initSpinner(exchange: ExchangeInfoDTO?) {
        if (exchange != null) {
            val symbolAdapter = ArrayAdapter(this, R.layout.spinner_item, exchange.symbols.map { it.symbol }).apply {
                setDropDownViewResource(R.layout.spinner_dropdown_item)
            }

            with(binding.entrySpinner) {
//                isEnabled = false
//                isClickable = false
                adapter = symbolAdapter
            }
        }
    }

    /**
     * 어댑터 설정
     * */
    private fun initAdapter() {
        inputDataAdapter = RecycleEntryInputData(coinDataList, binding.entryRecyclerView.layoutManager!!)

        binding.entryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EntryActivity, LinearLayoutManager.VERTICAL, false)
            adapter = inputDataAdapter
            addItemDecoration(DividerItemDecoration(this@EntryActivity, DividerItemDecoration.VERTICAL))
        }

        inputDataAdapter.addField()
    }

    /**
     * 각종 버튼 설정
     * */
    private fun initButtons() {
        // 추가 버튼
        binding.entryAddImage.setOnClickListener {
            inputDataAdapter.addField()
        }

        // 넘어가기 버튼
        binding.entryNextButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // 차트 코인 입력
            val showName = binding.entrySpinner.selectedItem as String

            GlobalData.showCoin.value = showName.substring(0..showName.length - 5)
            GlobalData.showTether.value = "USDT"

            binding.entryRecyclerView.layoutManager?.let { manager ->
                Log.d(TAG, inputDataAdapter.itemCount.toString())
                for (i in 0 until inputDataAdapter.itemCount) {
                    println("current : $i / $manager")
                    manager.findViewByPosition(i)?.let { itemView ->
                        val result = itemView.findViewById<TextView>(R.id.recycler_search_result_value)
                        val size = itemView.findViewById<EditText>(R.id.recycler_size_edit)
                        val leverage = itemView.findViewById<EditText>(R.id.recycler_leverage_edit)
                        val entryPrice = itemView.findViewById<EditText>(R.id.recycler_entry_price_edit)
                        val longShort = itemView.findViewById<RadioGroup>(R.id.recycler_long_short_radio)
                        val adl = itemView.findViewById<RadioGroup>(R.id.recycler_adl_radio)

                        val long = findViewById<RadioButton>(R.id.long_short_radio_long)
                        val short = findViewById<RadioButton>(R.id.long_short_radio_short)

                        val adl0 = findViewById<RadioButton>(R.id.adl_radio_0)
                        val adl1 = findViewById<RadioButton>(R.id.adl_radio_1)
                        val adl2 = findViewById<RadioButton>(R.id.adl_radio_2)
                        val adl3 = findViewById<RadioButton>(R.id.adl_radio_3)
                        val adl4 = findViewById<RadioButton>(R.id.adl_radio_4)

                        Log.d(TAG, "${result.text} / ${size.text} / ${leverage.text} / ${entryPrice.text} / ${longShort.checkedRadioButtonId} ")
                        val checkLongShort = when (longShort.checkedRadioButtonId) {
                            long.id -> {
                                LongShort.Long
                            }
                            short.id -> {
                                LongShort.Short
                            }
                            else -> {
                                LongShort.None
                            }
                        }
                        val checkADL = when (adl.checkedRadioButtonId) {
                            adl1.id -> ADL.ONE
                            adl2.id -> ADL.TWO
                            adl3.id -> ADL.THREE
                            adl4.id -> ADL.FOUR
                            else -> ADL.ZERO
                        }


                        val coinName = result.text
                        // 추가하는 조건
                        if (coinName.isNotBlank() and size.text.isNotBlank() and leverage.text.isNotBlank() and entryPrice.text.isNotBlank()
                            and (checkLongShort != LongShort.None)
                        ) {
                            FutureData.apply {
                                inputDataList.add(
                                    InputDataDTO(
                                        Pair(coinName.toString(), 0), size.text.toString(), leverage.text.toString(),
                                        entryPrice.text.toString(), checkLongShort
                                    ).also { it.adl = checkADL }
                                )
                            }
                        }
                    }

                    val cashBalance: BigDecimal = if (binding.entryCashBalanceEdit.text.toString().isBlank()) {
                        BigDecimal.ZERO
                    } else {
                        BigDecimal(binding.entryCashBalanceEdit.text.toString())
                    }

                    val futureBalance: BigDecimal = if (binding.entryFutureBalanceEdit.text.toString().isBlank()) {
                        BigDecimal.ZERO
                    } else {
                        BigDecimal(binding.entryFutureBalanceEdit.text.toString())
                    }

                    val revenuePeriod: String = if (binding.entryRevenueEdit.text.toString().isBlank()) {
                        "0"
                    } else {
                        binding.entryRevenueEdit.text.toString()
                    }


                    FutureData.let { model ->
                        model.scale =
                            BigDecimal(
                                model.inputDataList.find {
                                    it.coinName.first == (binding.entrySpinner.selectedItem as String)
                                }?.leverage?.toIntOrNull() ?: 1
                            )
                        model.cashBalance = cashBalance
                        model.futureBalance = futureBalance
                        model.revenuePeriod = revenuePeriod
                    }

                    startActivity(intent)
                }
            }
        }
    }
}