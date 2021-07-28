package com.project.vinance.view

import com.project.vinance.network.rest.vo.ExchangeInfoDTO
import com.project.vinance.view.sub.InputDataDTO
import java.math.BigDecimal

object FutureData {
    // 배율
    var scale: BigDecimal = BigDecimal.ONE

    // 입력 받은 데이터 리스트
    val inputDataList: MutableList<InputDataDTO> = mutableListOf()

    // 현재 잔고
    var cashBalance: BigDecimal = BigDecimal.ZERO

    // 선물 잔고
    var futureBalance: BigDecimal = BigDecimal.ZERO

    // 수익 기간
    var revenuePeriod: String = "0"

    // 코인 정보
    var exchangeInfo: ExchangeInfoDTO? = null
}