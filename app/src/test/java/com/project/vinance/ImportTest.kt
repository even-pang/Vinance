package com.project.vinance

import com.project.vinance.network.rest.vo.ExchangeInfoDTO
import com.project.vinance.view.FutureData
import com.project.vinance.view.GlobalData
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ImportTest {
    @Test
    fun network() {
        runBlocking {
            FutureData.exchangeInfo = GlobalData.getClient(GlobalData.FAPI_URL).getExchangeInfo()
            val result: ExchangeInfoDTO = FutureData.exchangeInfo!!

            println(result)
        }
    }
}