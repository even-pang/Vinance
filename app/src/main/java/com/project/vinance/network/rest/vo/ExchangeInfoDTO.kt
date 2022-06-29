package com.project.vinance.network.rest.vo

data class ExchangeInfoDTO(
    val timezone: String,
//    val serverTime: Long,
//    val futuresType: String,
//    val rateLimits: List<RateLimit>,
//    val assets: List<Asset>,
    val symbols: List<Symbol>
)

data class RateLimit(
    val rateLimitType: String,
    val interval: String,
    val intervalNum: Long,
    val limit: Long
)

data class Asset(
    val asset: String,
    val marginAvailable: Boolean,
    val autoAssetExchange: String
)

data class Symbol(
    val symbol: String,
    val pair: String,
    val contractType: String,
//    val deliveryDate: Long,
//    val onboardDate: Long,
    val status: String,
//    val maintMarginPercent: String,
//    val requiredMarginPercent: String,
//    val baseAsset: String,
    val quoteAsset: String,
//    val marginAsset: String,
    val pricePrecision: Int,
    val quantityPrecision: Int,
//    val baseAssetPrecision: Int,
//    val quotePrecision: Int,
//    val underlyingType: String,
//    val underlyingSubType: List<Any>,
//    val settlePlan: Int,
//    val triggerProtect: String,
//    val liquidationFee: String,
//    val marketTakeBound: String,
    val filters: List<Filter>,
//    val orderTypes: List<String>,
//    val timeInForce: List<String>
)

data class Filter(
    val minPrice: String,
    val maxPrice: String,
    val filterType: String,
    val tickSize: String
)