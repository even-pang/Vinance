package com.project.vinance

import java.math.BigDecimal

class Temp {
    fun test(symbol: String, nominalValue: BigDecimal) {
        var result = BigDecimal.ZERO
        var temp = nominalValue
        // BTCUSDT 170000
        when(symbol) {
            "BTCUSDT" -> {
                if (temp >= BigDecimal("5000000")) {
                    result += (temp - BigDecimal("4999999")) * BigDecimal("0.05")
                    temp = BigDecimal("4999999")
                }
                if (temp >= BigDecimal("1000000")) {
                    result += (temp - BigDecimal("999999")) * BigDecimal("0.025")
                    temp -= BigDecimal("999999")
                }
                if (temp >= BigDecimal("250000")) {
                    result += (temp - BigDecimal("249999")) * BigDecimal("0.01")
                    temp -= BigDecimal("249999")
                }
                if (temp >= BigDecimal("50000")) {
                    result += (temp - BigDecimal("49999")) * BigDecimal("0.005")
                    temp -= BigDecimal("49999")
                }
                if (temp < BigDecimal("50000")) {
                    result += temp * BigDecimal("0.004")
                }
            }
        }
    }
}