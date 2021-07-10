package com.project.vinance.network.socket;

import android.util.Pair;

import com.project.vinance.R;
import com.project.vinance.client.SubscriptionClient;
import com.project.vinance.client.model.enums.CandlestickInterval;
import com.project.vinance.client.model.market.OrderBookEntry;
import com.project.vinance.view.fragment.future.FutureFragment;
import com.project.vinance.view.implementation.TextChangeListenable;
import com.project.vinance.view.sub.NeedToChangeList;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class SocketClient {
    static NeedToChangeList changeList = NeedToChangeList.INSTANCE;
    static SubscriptionClient client = SubscriptionClient.create();

    static BigDecimal closeData = BigDecimal.ZERO;

    static BigDecimal limitPrice = BigDecimal.ZERO;
    static boolean isInitialDepth = true;
    static Pair<Integer, Integer> depthPosition = Pair.create(0, 0);

    private static boolean limitCondition = true;
    public static boolean canFutureBackground = true;
    private final static String SYMBOL = "btcusdt";

    final static BigDecimal myMoney = new BigDecimal("50000");
    final static BigDecimal scale = new BigDecimal("125");

    public static void futureChanger() {
        FutureFragment fragment = getFragment();
        /* SubscriptionClient client = SubscriptionClient.create();*/

        //시장가
        client.subscribeMarkPriceEvent(SYMBOL, ((event) -> {
//            fragment.changeText(R.id.future_order_book_market_price, event.getMarkPrice().setScale(2, RoundingMode.HALF_UP).toPlainString());
        }), null);

        //현재가
        client.subscribeCandlestickEvent(SYMBOL, CandlestickInterval.ONE_MINUTE, ((event) -> {
            closeData = event.getClose();
//            fragment.changeTextCurrent(R.id.future_contract_price, closeData);

            if (limitCondition) {
                limitPrice = closeData;
                limitCondition = false;
//                fragment.changeText(R.id.future_type_price, String.format(Locale.getDefault(), "%.2f", limitPrice));
//                fragment.changeText(R.id.main_type_market_max_value, String.valueOf(myMoney.divide(limitPrice, RoundingMode.HALF_UP).multiply(scale)));
            }
        }), null);

        // 사용 가능
//        fragment.changeText(R.id.future_trade_value, myMoney.setScale(2, RoundingMode.HALF_UP).toPlainString());

        // 전일대비
        client.subscribeSymbolTickerEvent(SYMBOL, ((event) -> {
//            fragment.changeTextChangePercent(R.id.future_title_percentage, event.getPriceChangePercent());
        }), null);

        Pair<List<Integer>, List<Integer>> sells = Pair.create(
                Arrays.asList(
                        R.id.future_sell1_left,
                        R.id.future_sell2_left,
                        R.id.future_sell3_left,
                        R.id.future_sell4_left,
                        R.id.future_sell5_left,
                        R.id.future_sell6_left,
                        R.id.future_sell7_left
                ), Arrays.asList(
                        R.id.future_sell1_right,
                        R.id.future_sell2_right,
                        R.id.future_sell3_right,
                        R.id.future_sell4_right,
                        R.id.future_sell5_right,
                        R.id.future_sell6_right,
                        R.id.future_sell7_right
                )
        );

        Pair<List<Integer>, List<Integer>> buys = Pair.create(
                Arrays.asList(
                        R.id.future_buy1_left,
                        R.id.future_buy2_left,
                        R.id.future_buy3_left,
                        R.id.future_buy4_left,
                        R.id.future_buy5_left,
                        R.id.future_buy6_left,
                        R.id.future_buy7_left
                ), Arrays.asList(
                        R.id.future_buy1_right,
                        R.id.future_buy2_right,
                        R.id.future_buy3_right,
                        R.id.future_buy4_right,
                        R.id.future_buy5_right,
                        R.id.future_buy6_right,
                        R.id.future_buy7_right
                )
        );

        // 호가(bid 구매(빨강), ask 판매(초록))
        client.subscribeBookDepthEvent(SYMBOL, 10, ((event) -> {
            final int LEN = 7;
            List<OrderBookEntry> bids = event.getBids().subList(0, LEN);
            List<OrderBookEntry> asks = event.getAsks().subList(0, LEN);
            if (isInitialDepth) {
                for (OrderBookEntry entry : bids) {
                    int scaleLeft = entry.getPrice().scale();
                    int scaleRight = entry.getQty().scale();

                    if (scaleLeft > depthPosition.first) {
                        depthPosition = Pair.create(scaleLeft, depthPosition.second);
                    }
                    if (scaleRight > depthPosition.second) {
                        depthPosition = Pair.create(depthPosition.first, scaleRight);
                    }
                }
                StringBuilder builder = new StringBuilder("0.");
                for (int i = 0; i < depthPosition.first - 1; i++) builder.append("0");
                builder.append("1");
//                fragment.changeText(R.id.future_order_book_scale, builder.toString());
                isInitialDepth = false;
            }

            for (int i = 0; i < bids.size(); i++) {
//                fragment.changeText(buys.first.get(i), bids.get(i).getPrice().setScale(depthPosition.first, RoundingMode.HALF_UP).toPlainString());
//                fragment.changeText(buys.second.get(i), bids.get(i).getQty().setScale(depthPosition.second, RoundingMode.HALF_UP).toPlainString());

//                fragment.changeText(sells.first.get(i), asks.get(i).getPrice().setScale(depthPosition.first, RoundingMode.HALF_UP).toPlainString());
//                fragment.changeText(sells.second.get(i), asks.get(i).getQty().setScale(depthPosition.second, RoundingMode.HALF_UP).toPlainString());

            }
            if (canFutureBackground) {
//                fragment.changeBackgroundWeight(bids, asks);
            }
        }), null);
    }

    public static void openLimitCondition() {
        limitCondition = true;
    }

    private static boolean isClosed = false;
    public static void unsubscribe() {
        client.unsubscribeAll();
        isClosed = true;
    }

    public static <T> T getFragment() {
        List<TextChangeListenable> lists = changeList.getFragmentList();

        for (TextChangeListenable list : lists) {
            if (list != null) {
                if (list instanceof FutureFragment) {
                    return (T) list;
                }
            }
        }

        return null;
    }

    public static void subscribe() {
        if (isClosed) {
            client = SubscriptionClient.create();

            futureChanger();
            isClosed = false;
        }
    }
}
