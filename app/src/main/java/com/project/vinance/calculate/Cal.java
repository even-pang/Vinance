package com.project.vinance.calculate;

import android.util.Log;

import androidx.annotation.NonNull;

import com.project.vinance.view.FutureData;
import com.project.vinance.view.recycler.RecycleFuturePosition;
import com.project.vinance.view.sub.InputDataDTO;
import com.project.vinance.view.sub.enumerate.LongShort;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Cal {
    public static boolean fin = false;

    public static void superCal() {
        Log.d("Cal2", "Init");
        try {
            //초기값 세팅
            FutureData instance = FutureData.INSTANCE;
            Log.d("Cal2", instance.getInputDataList().toString());
            for (int i = 0; i < instance.getInputDataList().size(); i++) {
                LongShort longShort = instance.getInputDataList().get(i).getLongShort();
                String symbol = instance.getInputDataList().get(i).getCoinName().getFirst();
                BigDecimal back = new BigDecimal("100");
                BigDecimal entryPrice = new BigDecimal(instance.getInputDataList().get(i).getEntryPrice());
                BigDecimal size = new BigDecimal(instance.getInputDataList().get(i).getSize());
                BigDecimal leverage = new BigDecimal(instance.getInputDataList().get(i).getLeverage());

                BigDecimal marketPrice = getMarketPrice(symbol);
                BigDecimal buyingPrice = entryPrice.multiply(size).divide(leverage, MathContext.DECIMAL128);
                BigDecimal entryNominalValue = entryPrice.multiply(size);

                //지정 0.02 시장 0.04
                BigDecimal commission = buyingPrice.multiply(new BigDecimal("0.0002")).multiply(leverage);

                //청산가격
                BigDecimal liqPNL = BigDecimal.ZERO;
                BigDecimal liqMaintenanceMarginPer = new BigDecimal(maintenanceMarginPer2(symbol, entryNominalValue));

                /* entryPrice * (liqMaintenanceMarginPer / 100) * size */
                BigDecimal liqMaintenanceMargin = entryPrice.multiply(liqMaintenanceMarginPer.divide(back, MathContext.DECIMAL128).multiply(size, MathContext.DECIMAL128), MathContext.DECIMAL128);
                instance.getInputDataList().get(i).setMargin(
                        buyingPrice.subtract(commission)
                );
                BigDecimal liqPrice = BigDecimal.ZERO;

                /* -1 * (1 - liqMaintenanceMargin / (buyingPrice - commission)) / leverage * 100 + (1 - liqMaintenanceMargin / (buyingPrice - commission)) */
                BigDecimal liqROE = new BigDecimal("-1").multiply(
                        BigDecimal.ONE.subtract(liqMaintenanceMargin.divide(buyingPrice.subtract(commission), MathContext.DECIMAL128))).divide(
                        leverage, MathContext.DECIMAL128).multiply(back).add(
                        (BigDecimal.ONE.subtract(liqMaintenanceMargin.divide(buyingPrice.subtract(commission), MathContext.DECIMAL128))).divide(
                                leverage, MathContext.DECIMAL128).multiply(BigDecimal.TEN)
                );

                BigDecimal maintenanceMargin = maintenanceMarginPer(symbol, marketPrice.multiply(size));
                instance.getInputDataList().get(i).setMaintenanceMargin(maintenanceMargin);
                BigDecimal margin = instance.getInputDataList().get(i).getMargin();
                BigDecimal pnl = instance.getInputDataList().get(i).getPnl();
                BigDecimal ssiBbuggu = longShort == LongShort.Short ? new BigDecimal("-1") : BigDecimal.ONE;

                instance.getInputDataList().get(i).setPnl((marketPrice.subtract(entryPrice)).multiply(size).multiply(ssiBbuggu));
                instance.getInputDataList().get(i).setRoe((BigDecimal.ONE.subtract(entryPrice.divide(marketPrice, MathContext.DECIMAL128))).multiply(back.multiply(leverage)).multiply(ssiBbuggu));

                // 0이랑 비교하니까 0보다 작으면 -1 같으면 0 크면 1
                RecycleFuturePosition.Companion.setOnceValue((marketPrice.subtract(entryPrice)).multiply(size).multiply(ssiBbuggu).compareTo(BigDecimal.ZERO));

                //Log.d("ABC", "liq : " + liqPNL.toPlainString() + ", liqMargin% : " + liqMaintenanceMarginPer.toPlainString() +
                //        ", liqMargin : " + liqMaintenanceMargin.toPlainString() + ", roe : " + liqROE.toPlainString());
//            -89.6399279855971194238847769553910820
                instance.getInputDataList().get(i).setDanger(
                        maintenanceMargin.divide(margin.add(pnl), MathContext.DECIMAL128).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
                );

                fin = true;

//            Log.d("0", FutureViewModel.INSTANCE.getInputDataList().get(i).toString());
                Log.d("Cal2", longShort.toString());
                if (longShort == LongShort.Long) {
                    Log.d("Cal_symbol", symbol);
                    Log.d("Cal_entryNominalValue", entryNominalValue.toPlainString());
                    Log.d("Cal_liqROE", liqROE.toPlainString());
                    Log.d("Cal_buyingPrice", buyingPrice.toPlainString());
                    Log.d("Cal_commission", commission.toPlainString());
                    Log.d("Cal_liqPNL", liqPNL.toPlainString());

//                    Log.d("Cal", "(entryNominalValue + (entryNominalValue * liqROE)) / (buyingPrice - commission) + liqPNL < 0");
//                    Log.d("Cal_cal", "(" + entryNominalValue.toPlainString() + "+(" + entryNominalValue.toPlainString() + "*" + liqROE.toPlainString() +
//                            "))/(" + buyingPrice.toPlainString() + "-" + commission.toPlainString() + ")+" + liqPNL.toPlainString());
                    BigDecimal ak47 = maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128))));
                    BigDecimal m16 = buyingPrice.subtract(commission).add(liqPNL);
                    Log.d("Cal22222222222", ak47.toPlainString());
                    Log.d("Cal22222222222", m16.toPlainString());

                    /*symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(BigDecimal.ONE) < 0*/
                    // 유지마진_퍼센트(진입가 + (진입가 * roe / 100)) / (구매가 - 수수료 + pnl)
                    Log.d("Cal씨이이이잇펄", "ak47: " + maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))) + " || " + buyingPrice.subtract(commission).add(liqPNL));
                    while (maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(BigDecimal.ONE) < 0) {
                        //Log.d("Cal씨이이이잇펄", "ak47: " + maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))) + " || " + buyingPrice.subtract(commission).add(liqPNL));
                        if(maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).compareTo(BigDecimal.ZERO) < 0) break;
                        if (maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.009")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.01"));
                        } else if (maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.09")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.001"));
                        } else if (maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.9")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.0001"));
                        } else if (maintenanceMarginPer(symbol, (entryNominalValue.add(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).add(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.99")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.00001"));
                        } else {
                            liqROE = liqROE.add(new BigDecimal("-0.000001"));
                        }
                        liqPrice = entryPrice.add(entryPrice.multiply(liqROE).divide(back, MathContext.DECIMAL128));
                        liqPNL = (liqPrice.subtract(entryPrice)).multiply(size);
                        //Log.d("1", FutureViewModel.INSTANCE.getInputDataList().toString());
//                    Log.d("1", liqROE.toPlainString() + " || " + liqPrice.toPlainString());
                    }
                } else {
                    //유지마진/마진비율 == 1 이될때 끝난다 이성은씨 돌아오세요???
                    //while ((liqMaintenanceMargin.subtract(liqMaintenanceMargin.multiply(liqROE).divide(back))).divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(BigDecimal.ONE) < 0) {
                    while (maintenanceMarginPer(symbol, (entryNominalValue.subtract(entryNominalValue.multiply(liqROE).divide(back, MathContext.DECIMAL128)))).divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(BigDecimal.ONE) < 0) {
                        BigDecimal ak47 = maintenanceMarginPer(symbol, (entryNominalValue.subtract(entryNominalValue.multiply(liqROE).divide(back))));
                        if (ak47.divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.09")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.3"));
                        } else if (ak47.divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.9")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.03"));
                        } else if (ak47.divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.99")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.003"));
                        } else if (ak47.divide(buyingPrice.subtract(commission).subtract(liqPNL), MathContext.DECIMAL128).compareTo(new BigDecimal("0.999")) < 0) {
                            liqROE = liqROE.add(new BigDecimal("-0.0003"));
                        } else {
                            liqROE = liqROE.add(new BigDecimal("-0.00003"));
                        }
                        liqPrice = entryPrice.subtract(entryPrice.multiply(liqROE).divide(back, MathContext.DECIMAL128));
                        liqPNL = (liqPrice.subtract(entryPrice)).multiply(size);

                        //Log.d("2", FutureViewModel.INSTANCE.getInputDataList().toString());
//                    Log.d("2", liqPNL.toPlainString() + " || " + liqPrice.toPlainString());
                    }
                }

                instance.getInputDataList().get(i).setLiquidationPrice(liqPrice);

//            Log.d("3", FutureViewModel.INSTANCE.getInputDataList().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Cal2", "SuperCal End");
    }

    public static void subCal() {
        //씨뻐꺼
        FutureData instance = FutureData.INSTANCE;
        for (int i = 0; i < instance.getInputDataList().size(); i++) {
            String symbol = instance.getInputDataList().get(i).getCoinName().getFirst();
            BigDecimal EntryPrice = new BigDecimal(instance.getInputDataList().get(i).getEntryPrice());
            BigDecimal size = new BigDecimal(instance.getInputDataList().get(i).getSize());
            BigDecimal leverage = new BigDecimal(instance.getInputDataList().get(i).getLeverage());
            BigDecimal back = new BigDecimal("100");
            BigDecimal MarketPrice = getMarketPrice(symbol);
            BigDecimal liqMaintenanceMarginPer = maintenanceMarginPer(symbol, EntryPrice.multiply(size).divide(leverage, MathContext.DECIMAL128));
            BigDecimal maintenanceMargin = maintenanceMarginPer(symbol, MarketPrice.multiply(size));
            BigDecimal margin = instance.getInputDataList().get(i).getMargin();
            BigDecimal pnl = instance.getInputDataList().get(i).getPnl();

            LongShort longShort = instance.getInputDataList().get(i).getLongShort();
            BigDecimal ssiBbuggu = longShort == LongShort.Short ? new BigDecimal("-1") : BigDecimal.ONE;
            instance.getInputDataList().get(i).setDanger(
                    maintenanceMargin.divide(margin.add(pnl), MathContext.DECIMAL128).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)
            );
            instance.getInputDataList().get(i).setMaintenanceMargin(maintenanceMargin);
            instance.getInputDataList().get(i).setPnl((MarketPrice.subtract(EntryPrice)).multiply(size).multiply(ssiBbuggu));
            instance.getInputDataList().get(i).setRoe((BigDecimal.ONE.subtract(EntryPrice.divide(MarketPrice, MathContext.DECIMAL128))).multiply(back.multiply(leverage)).multiply(ssiBbuggu));
        }

//        Log.d("Cal", FutureData.INSTANCE.getInputDataList().toString());
    }

    @NonNull
    private static BigDecimal getMarketPrice(String symbol) {
        for (InputDataDTO inputData : FutureData.INSTANCE.getInputDataList()) {
            if (inputData.getCoinName().getFirst().equals(symbol)) {
                return inputData.getMarketPrice();
            }
        }

        return BigDecimal.ONE;
    }


    public static BigDecimal maintenanceMarginPer(String symbol, BigDecimal nominalValue) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal temp = nominalValue;

        //Log.d("Cal_maint", symbol + "/" + nominalValue.toPlainString());

        switch (symbol) {
            case "BTCUSDT": {//125배
                if (temp.compareTo(new BigDecimal("300000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("300000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("300000000");
                }
                if (temp.compareTo(new BigDecimal("200000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("199999999"))).multiply(new BigDecimal("0.25")));
                    temp = new BigDecimal("199999999");
                }
                if (temp.compareTo(new BigDecimal("100000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999999"))).multiply(new BigDecimal("0.15")));
                    temp = new BigDecimal("99999999");
                }
                if (temp.compareTo(new BigDecimal("50000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("49999999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("49999999");
                }
                if (temp.compareTo(new BigDecimal("20000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("19999999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("19999999");
                }
                if (temp.compareTo(new BigDecimal("5000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("4999999");
                }
                if (temp.compareTo(new BigDecimal("1000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("999999"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("999999");
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.01")));
                    temp = new BigDecimal("249999");
                }
                if (temp.compareTo(new BigDecimal("50000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("49999"))).multiply(new BigDecimal("0.005")));
                    temp = new BigDecimal("49999");
                }
                result = result.add((temp.multiply(new BigDecimal("0.004"))));
                break;
            }
            case "ETHUSDT": {//100배
                if (temp.compareTo(new BigDecimal("20000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("20000000"))).multiply(new BigDecimal("0.25")));
                    temp = new BigDecimal("20000000");
                }
                if (temp.compareTo(new BigDecimal("10000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("9999999"))).multiply(new BigDecimal("0.15")));
                    temp = new BigDecimal("9999999");
                }
                if (temp.compareTo(new BigDecimal("5000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("4999999");
                }
                if (temp.compareTo(new BigDecimal("2000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("1999999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("1999999");
                }
                if (temp.compareTo(new BigDecimal("1000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("999999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("999999");
                }
                if (temp.compareTo(new BigDecimal("500000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("499999"))).multiply(new BigDecimal("0.02")));
                    temp = new BigDecimal("499999");
                }
                if (temp.compareTo(new BigDecimal("100000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999"))).multiply(new BigDecimal("0.01")));
                    temp = new BigDecimal("99999");
                }
                if (temp.compareTo(new BigDecimal("10000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("9999"))).multiply(new BigDecimal("0.0065")));
                    temp = new BigDecimal("9999");
                }
                result = result.add(temp.multiply(new BigDecimal("0.005")));
                break;
            }
            case "ADAUSDT":
            case "BNBUSDT":
            case "DOTUSDT":
            case "EOSUSDT":
            case "ETCUSDT":
            case "LINKUSDT":
            case "LTCUSDT":
            case "TRXUSDT":
            case "XLMUSDT":
            case "XMRUSDT":
            case "XRPUSDT":
            case "XTZUSDT":
            case "BCHUSDT": {//75배
                if (temp.compareTo(new BigDecimal("10000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("9999999"))).multiply(new BigDecimal("0.25")));
                    temp = new BigDecimal("9999999");
                }
                if (temp.compareTo(new BigDecimal("5000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999999"))).multiply(new BigDecimal("0.15")));
                    temp = new BigDecimal("4999999");
                }
                if (temp.compareTo(new BigDecimal("2000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("1999999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("1999999");
                }
                if (temp.compareTo(new BigDecimal("1000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("999999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("999999");
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("249999");
                }
                if (temp.compareTo(new BigDecimal("50000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("49999"))).multiply(new BigDecimal("0.02")));
                    temp = new BigDecimal("49999");
                }
                if (temp.compareTo(new BigDecimal("10000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("9999"))).multiply(new BigDecimal("0.01")));
                    temp = new BigDecimal("9999");
                }
                result = result.add(temp.multiply(new BigDecimal("0.0065")));
                break;
            }
            case "ALGOUSDT":
            case "ALPHAUSDT":
            case "ATOMUSDT":
            case "AVAXUSDT":
            case "AXSUSDT":
            case "BALUSDT":
            case "BANDUSDT":
            case "BATUSDT":
            case "BELUSDT":
            case "BLZUSDT":
            case "BZRXUSDT":
            case "COMPUSDT":
            case "CRVUSDT":
            case "CVCUSDT":
            case "DASHUSDT":
            case "DEFIUSDT":
            case "EGLDUSDT":
            case "ENJUSDT":
            case "FLMUSDT":
            case "FTMUSDT":
            case "HNTUSDT":
            case "ICXUSDT":
            case "IOSTUSDT":
            case "IOTAUSDT":
            case "KAVAUSDT":
            case "KNCUSDT":
            case "KSMUSDT":
            case "LRCUSDT":
            case "MKRUSDT":
            case "NEARUSDT":
            case "NEOUSDT":
            case "OCEANUSDT":
            case "OMGUSDT":
            case "ONTUSDT":
            case "QTUMUSDT":
            case "RENUSDT":
            case "RLCUSDT":
            case "RSRUSDT":
            case "RUNEUSDT":
            case "SNXUSDT":
            case "SRMUSDT":
            case "STORJUSDT":
            case "SUSHIUSDT":
            case "SXPUSDT":
            case "TOMOUSDT":
            case "TRBUSDT":
            case "VETUSDT":
            case "WAVESUSDT":
            case "YFIIUSDT":
            case "YFIUSDT":
            case "ZECUSDT":
            case "ZILUSDT":
            case "ZRXUSDT":
            case "ZENUSDT":
            case "SKLUSDT":
            case "GRTUSDT":
            case "CTKUSDT":
            case "BTSUSDT":
            case "LITUSDT":
            case "UNFIUSDT":
            case "DODOUSDT":
            case "REEFUSDT":
            case "RVNUSDT":
            case "SFPUSDT":
            case "XEMUSDT":
            case "BTCSTUSDT":
            case "COTIUSDT":
            case "CHRUSDT":
            case "ALICEUSDT":
            case "ONEUSDT":
            case "LINAUSDT":
            case "STMXUSDT":
            case "DENTUSDT":
            case "CELRUSDT":
            case "HOTUSDT":
            case "MTLUSDT":
            case "OGNUSDT":
            case "BTTUSDT": {//50배
                if (temp.compareTo(new BigDecimal("1000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("1000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("1000000");
                    //return "50.0";
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("249999");
//                    return "12.5";
                }
                if (temp.compareTo(new BigDecimal("100000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("99999");
//                    return "10.0";
                }
                if (temp.compareTo(new BigDecimal("25000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("24999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("24999");
//                    return "5.0";
                }
                if (temp.compareTo(new BigDecimal("5000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("24999");
//                    return "2.5";
                }
                result = result.add(temp.multiply(new BigDecimal("0.01")));
//                    return "1.0";
            }
            break;
            case "1INCHUSDT":
            case "ANKRUSDT":
            case "AKROUSDT":
            case "SANDUSDT":
            case "LUNAUSDT":
            case "CHZUSDT": {//50배
                if (temp.compareTo(new BigDecimal("1000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("1000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("1000000");
//                    return "50.0";
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("249999");
//                    return "12.5";
                }
                if (temp.compareTo(new BigDecimal("100000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("99999");
//                    return "10.0";
                }
                if (temp.compareTo(new BigDecimal("25000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("24999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("24999");
//                    return "5.0";
                }
                if (temp.compareTo(new BigDecimal("5000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("5000"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("5000");
//                    return "2.5";
                }
                result = result.add(temp.multiply(new BigDecimal("0.012")));
                break;
            }
            case "FILUSDT":
            case "AAVEUSDT":
            case "UNIUSDT":
            case "THETAUSDT": {//50배
                if (temp.compareTo(new BigDecimal("10000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("9999999"))).multiply(new BigDecimal("0.25")));
                    temp = new BigDecimal("9999999");
//                    return "25.0";
                }
                if (temp.compareTo(new BigDecimal("5000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999999"))).multiply(new BigDecimal("0.165")));
                    temp = new BigDecimal("4999999");
//                    return "16.5";
                }
                if (temp.compareTo(new BigDecimal("2000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("1999999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("1999999");
//                    return "12.5";
                }
                if (temp.compareTo(new BigDecimal("1000000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("999999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("999999");
//                    return "10.0";
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("249999");
//                    return "5.0";
                }
                if (temp.compareTo(new BigDecimal("50000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("50000"))).multiply(new BigDecimal("0.02")));
                    temp = new BigDecimal("50000");
//                    return "2.0";
                }
                result = result.add((temp.subtract(new BigDecimal("5000"))).multiply(new BigDecimal("0.01")));
            }
            break;
            case "DOGEUSDT":
            case "SOLUSDT":
            case "MATICUSDT": {//50배
                if (temp.compareTo(new BigDecimal("30000000")) <= 0 && temp.compareTo(new BigDecimal("1000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("1000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("1000000");
                }
                if (temp.compareTo(new BigDecimal("750000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("749999"))).multiply(new BigDecimal("0.25")));
                    temp = new BigDecimal("749999");
                }
                if (temp.compareTo(new BigDecimal("500000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("499999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("499999");
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("249999");
                }
                if (temp.compareTo(new BigDecimal("150000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("149999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("149999");
                }
                if (temp.compareTo(new BigDecimal("50000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("49999"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("49999");
                }
                result = result.add(temp.multiply(new BigDecimal("0.01")));
                break;
            }
            case "NKNUSDT":
            case "DGBUSDT":
            case "SHIBUSDT":
            case "ICPUSDT":
            case "BAKEUSDT": {//50배
                if (temp.compareTo(new BigDecimal("1000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("1000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("1000000");
                }
                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("249999");
                }
                if (temp.compareTo(new BigDecimal("100000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("99999");
                }
                if (temp.compareTo(new BigDecimal("25000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("24999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("24999");
                }
                if (temp.compareTo(new BigDecimal("5000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("4999");
                }
                result = result.add(temp.multiply(new BigDecimal("0.01")));
                break;
            }
            case "SCUSDT":
            case "BTCDOMUSDT":
            case "KEEPUSDT": {//25배
                if (temp.compareTo(new BigDecimal("1000000")) > 0) {
                    result = result.add((temp.subtract(new BigDecimal("1000000"))).multiply(new BigDecimal("0.5")));
                    temp = new BigDecimal("1000000");
                    //return "50.0";
                }

                if (temp.compareTo(new BigDecimal("250000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("249999"))).multiply(new BigDecimal("0.125")));
                    temp = new BigDecimal("249999");
                    //return "10.0";
                }
                if (temp.compareTo(new BigDecimal("100000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("99999"))).multiply(new BigDecimal("0.1")));
                    temp = new BigDecimal("99999");
                    //return "5.0";
                }
                if (temp.compareTo(new BigDecimal("25000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("24999"))).multiply(new BigDecimal("0.05")));
                    temp = new BigDecimal("24999");
                    //return "2.5";
                }
                if (temp.compareTo(new BigDecimal("5000")) >= 0) {
                    result = result.add((temp.subtract(new BigDecimal("4999"))).multiply(new BigDecimal("0.025")));
                    temp = new BigDecimal("4999");
                    //return "2.5";
                }
                result = result.add(temp.multiply(new BigDecimal("0.01")));
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
        //Log.d("Cal result", result.toPlainString());
        return result;
    }

    public static String maintenanceMarginPer2(String symbol, BigDecimal nominalValue) {
        switch (symbol) {
            case "BTCUSDT": {//125배
                if (nominalValue.compareTo(new BigDecimal("50000")) < 0) return "0.4";
                else if (nominalValue.compareTo(new BigDecimal("50000")) > 0 && nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "0.5"; //0.005
                else if (nominalValue.compareTo(new BigDecimal("250000")) > 0 && nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "1.0"; //0.01
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0 && nominalValue.compareTo(new BigDecimal("5000000")) <= 0) return "2.5"; //0.025
                else if (nominalValue.compareTo(new BigDecimal("5000000")) > 0 && nominalValue.compareTo(new BigDecimal("20000000")) <= 0) return "5.0"; //0.05
                else if (nominalValue.compareTo(new BigDecimal("20000000")) > 0 && nominalValue.compareTo(new BigDecimal("50000000")) <= 0) return "10.0"; //0.1
                else if (nominalValue.compareTo(new BigDecimal("50000000")) > 0 && nominalValue.compareTo(new BigDecimal("100000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("100000000")) > 0 && nominalValue.compareTo(new BigDecimal("200000000")) <= 0) return "15.0";
                else if (nominalValue.compareTo(new BigDecimal("200000000")) > 0 && nominalValue.compareTo(new BigDecimal("300000000")) <= 0) return "25.0";
                else if (nominalValue.compareTo(new BigDecimal("300000000")) > 0 && nominalValue.compareTo(new BigDecimal("500000000")) <= 0) return "50.0";
                break;
            }
            case "ETHUSDT": {//100배
                if (nominalValue.compareTo(new BigDecimal("10000")) <= 0) return "0.5";
                else if (nominalValue.compareTo(new BigDecimal("100000")) <= 0) return "0.65";
                else if (nominalValue.compareTo(new BigDecimal("500000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "2.0";
                else if (nominalValue.compareTo(new BigDecimal("2000000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("5000000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("10000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("20000000")) <= 0) return "15.0";
                else if (nominalValue.compareTo(new BigDecimal("20000000")) > 0) return "25.0";
                break;
            }
            case "ADAUSDT":
            case "BNBUSDT":
            case "DOTUSDT":
            case "EOSUSDT":
            case "ETCUSDT":
            case "LINKUSDT":
            case "LTCUSDT":
            case "TRXUSDT":
            case "XLMUSDT":
            case "XMRUSDT":
            case "XRPUSDT":
            case "XTZUSDT":
            case "BCHUSDT": {//75배
                if (nominalValue.compareTo(new BigDecimal("10000")) <= 0) return "0.65";
                else if (nominalValue.compareTo(new BigDecimal("50000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "2.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("2000000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("5000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("10000000")) <= 0) return "15.0";
                else if (nominalValue.compareTo(new BigDecimal("10000000")) > 0) return "25.0";
                break;
            }
            case "ALGOUSDT":
            case "ALPHAUSDT":
            case "ATOMUSDT":
            case "AVAXUSDT":
            case "AXSUSDT":
            case "BALUSDT":
            case "BANDUSDT":
            case "BATUSDT":
            case "BELUSDT":
            case "BLZUSDT":
            case "BZRXUSDT":
            case "COMPUSDT":
            case "CRVUSDT":
            case "CVCUSDT":
            case "DASHUSDT":
            case "DEFIUSDT":
            case "EGLDUSDT":
            case "ENJUSDT":
            case "FLMUSDT":
            case "FTMUSDT":
            case "HNTUSDT":
            case "ICXUSDT":
            case "IOSTUSDT":
            case "IOTAUSDT":
            case "KAVAUSDT":
            case "KNCUSDT":
            case "KSMUSDT":
            case "LRCUSDT":
            case "MKRUSDT":
            case "NEARUSDT":
            case "NEOUSDT":
            case "OCEANUSDT":
            case "OMGUSDT":
            case "ONTUSDT":
            case "QTUMUSDT":
            case "RENUSDT":
            case "RLCUSDT":
            case "RSRUSDT":
            case "RUNEUSDT":
            case "SNXUSDT":
            case "SRMUSDT":
            case "STORJUSDT":
            case "SUSHIUSDT":
            case "SXPUSDT":
            case "TOMOUSDT":
            case "TRBUSDT":
            case "VETUSDT":
            case "WAVESUSDT":
            case "YFIIUSDT":
            case "YFIUSDT":
            case "ZECUSDT":
            case "ZILUSDT":
            case "ZRXUSDT":
            case "ZENUSDT":
            case "SKLUSDT":
            case "GRTUSDT":
            case "CTKUSDT":
            case "BTSUSDT":
            case "LITUSDT":
            case "UNFIUSDT":
            case "DODOUSDT":
            case "REEFUSDT":
            case "RVNUSDT":
            case "SFPUSDT":
            case "XEMUSDT":
            case "BTCSTUSDT":
            case "COTIUSDT":
            case "CHRUSDT":
            case "ALICEUSDT":
            case "ONEUSDT":
            case "LINAUSDT":
            case "STMXUSDT":
            case "DENTUSDT":
            case "CELRUSDT":
            case "HOTUSDT":
            case "MTLUSDT":
            case "OGNUSDT":
            case "BTTUSDT": {//50배
                if (nominalValue.compareTo(new BigDecimal("5000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("25000")) <= 0) return "2.5";
                else if (nominalValue.compareTo(new BigDecimal("100000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0) return "50.0";
                break;
            }
            case "1INCHUSDT":
            case "ANKRUSDT":
            case "AKROUSDT":
            case "SANDUSDT":
            case "LUNAUSDT":
            case "CHZUSDT": {//50배
                if (nominalValue.compareTo(new BigDecimal("5000")) <= 0) return "1.2";
                else if (nominalValue.compareTo(new BigDecimal("25000")) <= 0) return "2.5";
                else if (nominalValue.compareTo(new BigDecimal("100000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0) return "50.0";
                break;
            }
            case "FILUSDT":
            case "AAVEUSDT":
            case "UNIUSDT":
            case "THETAUSDT": {//50배
                if (nominalValue.compareTo(new BigDecimal("50000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "2.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("2000000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("5000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("10000000")) <= 0) return "16.5";
                else if (nominalValue.compareTo(new BigDecimal("10000000")) > 0) return "25.0";
                break;
            }
            case "DOGEUSDT":
            case "SOLUSDT":
            case "MATICUSDT": {//50배
                if (nominalValue.compareTo(new BigDecimal("50000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("150000")) <= 0) return "2.5";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("500000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("750000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "25.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0 && nominalValue.compareTo(new BigDecimal("30000000")) <= 0) return "50.0";
                break;
            }
            case "NKNUSDT":
            case "DGBUSDT":
            case "SHIBUSDT":
            case "ICPUSDT":
            case "BAKEUSDT": {//50배
                if (nominalValue.compareTo(new BigDecimal("5000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("25000")) <= 0) return "2.5";
                else if (nominalValue.compareTo(new BigDecimal("100000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0) return "50.0";
                break;
            }
            case "SCUSDT":
            case "BTCDOMUSDT":
            case "KEEPUSDT": {//25배
                if (nominalValue.compareTo(new BigDecimal("5000")) <= 0) return "1.0";
                else if (nominalValue.compareTo(new BigDecimal("25000")) <= 0) return "2.5";
                else if (nominalValue.compareTo(new BigDecimal("100000")) <= 0) return "5.0";
                else if (nominalValue.compareTo(new BigDecimal("250000")) <= 0) return "10.0";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) <= 0) return "12.5";
                else if (nominalValue.compareTo(new BigDecimal("1000000")) > 0) return "50.0";
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
        return null;
    }
}
