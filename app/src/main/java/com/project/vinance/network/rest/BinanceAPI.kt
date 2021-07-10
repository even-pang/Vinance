package com.project.vinance.network.rest

import com.project.vinance.network.rest.vo.DepthVO
import com.project.vinance.network.rest.vo.MarkPriceVO
import com.project.vinance.network.rest.vo.SymbolPriceVO
import com.project.vinance.network.rest.vo.TickerPriceChangeVO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceAPI {
    companion object {
        const val BASE_URL = "https://fapi.binance.com"
    }

    // 호가
    @GET("/fapi/v1/depth")
    fun getDepth(@Query("symbol") symbol: String, @Query("limit") limit: Int? = 10): Call<DepthVO>

    // 현재가
    @GET("/fapi/v1/ticker/price")
    fun getSymbolPrice(@Query("symbol") symbol: String): Call<SymbolPriceVO>

    // 시장가, 펀딩/카운트다운
    @GET("/fapi/v1/premiumIndex")
    fun getMarkPrice(@Query("symbol") symbol: String): Call<MarkPriceVO>

    // 전일대비
    @GET("/fapi/v1/ticker/24hr")
    fun getTickerPriceChange(@Query("symbol") symbol: String): Call<TickerPriceChangeVO>
}