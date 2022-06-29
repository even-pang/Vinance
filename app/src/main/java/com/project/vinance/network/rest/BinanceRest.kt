package com.project.vinance.network.rest

import com.project.vinance.network.rest.vo.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceRest {
    companion object {
        const val BASE_URL = "https://fapi.binance.com"
        const val NON_F_BASE_URL = "https://api.binance.com"
    }

    // 호가
    @GET("/fapi/v1/depth")
    suspend fun getDepth(@Query("symbol") symbol: String, @Query("limit") limit: Int? = 10): DepthVO

    // 현재가
    @GET("/fapi/v1/ticker/price")
    suspend fun getSymbolPrice(@Query("symbol") symbol: String): SymbolPriceVO

    // 시장가, 펀딩/카운트다운
    @GET("/fapi/v1/premiumIndex")
    suspend fun getMarkPrice(@Query("symbol") symbol: String): MarkPriceVO

    // 전일대비
    @GET("/fapi/v1/ticker/24hr")
    suspend fun getTickerPriceChange(@Query("symbol") symbol: String): TickerPriceChangeVO

    // 코인 정보
    @GET("fapi/v1/exchangeInfo")
    suspend fun getExchangeInfo(): ExchangeInfoDTO

    @GET("/api/v1/ticker/price")
    suspend fun getPrice(@Query("symbol") symbol: String): Price2VO
}