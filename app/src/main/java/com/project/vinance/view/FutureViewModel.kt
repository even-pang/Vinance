package com.project.vinance.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal

object FutureViewModel : ViewModel() {
    const val ORDER_LIST_SIZE = 7

    // 사용 가능
    val usableMoney: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)
    // 배율
    var scale: Int = 125

    // 호가 데이터
    val bidsAndAsks: MutableLiveData<Pair<List<Pair<String, String>>, List<Pair<String, String>>>> = MutableLiveData()
//    val bids: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
//    val asks: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()

    // 테더(USDT)
    val tether: MutableLiveData<String> = MutableLiveData("")

    // 코인 유형(BTC, ETH 등)
    val coin: MutableLiveData<String> = MutableLiveData("")

    // 전일대비
    val pricePercent: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 현재가(최근가)
    val contractPrice: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 시장가(회색)
    val markPrice: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 펀딩
    val fundingRate: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)

    // 다음 펀딩 시간
    val fundingTime: MutableLiveData<Long> = MutableLiveData(0L)

    // 현재가 왼쪽 부분
    val contractPriceLeft: MutableLiveData<BigDecimal> = MutableLiveData(BigDecimal.ZERO)
}