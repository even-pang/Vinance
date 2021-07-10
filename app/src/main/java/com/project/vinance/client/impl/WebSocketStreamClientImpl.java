package com.project.vinance.client.impl;

import com.project.vinance.client.SubscriptionClient;
import com.project.vinance.client.SubscriptionErrorHandler;
import com.project.vinance.client.SubscriptionListener;
import com.project.vinance.client.SubscriptionOptions;
import com.project.vinance.client.model.enums.CandlestickInterval;
import com.project.vinance.client.model.event.AggregateTradeEvent;
import com.project.vinance.client.model.event.CandlestickEvent;
import com.project.vinance.client.model.event.LiquidationOrderEvent;
import com.project.vinance.client.model.event.MarkPriceEvent;
import com.project.vinance.client.model.event.OrderBookEvent;
import com.project.vinance.client.model.event.SymbolBookTickerEvent;
import com.project.vinance.client.model.event.SymbolMiniTickerEvent;
import com.project.vinance.client.model.event.SymbolTickerEvent;
import com.project.vinance.client.model.user.UserDataUpdateEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class WebSocketStreamClientImpl implements SubscriptionClient {

    private final SubscriptionOptions options;
    private WebSocketWatchDog watchDog;

    private final WebsocketRequestImpl requestImpl;

    private final List<WebSocketConnection> connections = new LinkedList<>();

    WebSocketStreamClientImpl(SubscriptionOptions options) {
        this.watchDog = null;
        this.options = Objects.requireNonNull(options);

        this.requestImpl = new WebsocketRequestImpl();
    }

    private <T> void createConnection(WebsocketRequest<T> request, boolean autoClose) {
        if (watchDog == null) {
            watchDog = new WebSocketWatchDog(options);
        }
        WebSocketConnection connection = new WebSocketConnection(request, watchDog, autoClose);
        if (autoClose == false) {
            connections.add(connection);
        }
        connection.connect();
    }

    private <T> void createConnection(WebsocketRequest<T> request) {
        createConnection(request, false);
    }

    @Override
    public void unsubscribeAll() {
        for (WebSocketConnection connection : connections) {
            watchDog.onClosedNormally(connection);
            connection.close();
        }
        connections.clear();
    }

    @Override
    public void subscribeAggregateTradeEvent(String symbol,
            SubscriptionListener<AggregateTradeEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeAggregateTradeEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeMarkPriceEvent(String symbol,
            SubscriptionListener<MarkPriceEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeMarkPriceEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeCandlestickEvent(String symbol, CandlestickInterval interval,
            SubscriptionListener<CandlestickEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeCandlestickEvent(symbol, interval, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeSymbolMiniTickerEvent(String symbol,
            SubscriptionListener<SymbolMiniTickerEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeSymbolMiniTickerEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeAllMiniTickerEvent(SubscriptionListener<List<SymbolMiniTickerEvent>> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeAllMiniTickerEvent(subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeSymbolTickerEvent(String symbol,
            SubscriptionListener<SymbolTickerEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeSymbolTickerEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeAllTickerEvent(SubscriptionListener<List<SymbolTickerEvent>> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeAllTickerEvent(subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeSymbolBookTickerEvent(String symbol,
            SubscriptionListener<SymbolBookTickerEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeSymbolBookTickerEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeAllBookTickerEvent(SubscriptionListener<SymbolBookTickerEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeAllBookTickerEvent(subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeSymbolLiquidationOrderEvent(String symbol,
            SubscriptionListener<LiquidationOrderEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeSymbolLiquidationOrderEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeAllLiquidationOrderEvent(SubscriptionListener<LiquidationOrderEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeAllLiquidationOrderEvent(subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeBookDepthEvent(String symbol, Integer limit,
            SubscriptionListener<OrderBookEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeBookDepthEvent(symbol, limit, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeDiffDepthEvent(String symbol,
            SubscriptionListener<OrderBookEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeDiffDepthEvent(symbol, subscriptionListener, errorHandler));
    }

    @Override
    public void subscribeUserDataEvent(String listenKey,
            SubscriptionListener<UserDataUpdateEvent> subscriptionListener, 
            SubscriptionErrorHandler errorHandler) {
        createConnection(
                requestImpl.subscribeUserDataEvent(listenKey, subscriptionListener, errorHandler));
    }


}
