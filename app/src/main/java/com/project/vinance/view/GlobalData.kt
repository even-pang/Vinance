package com.project.vinance.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.GsonBuilder
import com.project.vinance.network.rest.BinanceRest
import com.project.vinance.network.rest.vo.ExchangeInfoDTO
import com.project.vinance.view.sub.InputDataDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

object GlobalData : ViewModel() {
    const val FAPI_URL = "https://fapi.binance.com/"
    const val API_URL = "https://api.binance.com/"

    private fun instance(baseUrl: String): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())).build()
    }

    fun getClient(baseUrl: String): BinanceRest {
        return instance(baseUrl).create(BinanceRest::class.java)
    }

    const val ORDER_LIST_SIZE = 8

    // 호가 데이터
    val bidsAndAsks: MutableLiveData<Pair<List<Pair<String, String>>, List<Pair<String, String>>>> = MutableLiveData()
//    val bids: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
//    val asks: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()

    // 테더(USDT)
    val showTether: MutableLiveData<String> = MutableLiveData("")

    // 코인 유형(BTC, ETH 등)
    val showCoin: MutableLiveData<String> = MutableLiveData("")

    // 전일대비
    val pricePercent: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 현재가(최근가)
    val contractPrice: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 시장가(회색)
    val markPrice: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ONE)

    // 펀딩
    val fundingRate: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 다음 펀딩 시간
    val fundingTime: MutableLiveData<Long> = MutableLiveData(0L)

    // 현재가 왼쪽 부분
    val contractPriceLeft: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 코인 리스트
    var coinList: List<String> = emptyList()

}
