package com.project.vinance.client.impl;

import com.project.vinance.client.RequestOptions;
import com.project.vinance.client.SubscriptionClient;
import com.project.vinance.client.SubscriptionOptions;
import com.project.vinance.client.SyncRequestClient;
import java.net.URI;

public final class BinanceApiInternalFactory {

    private static final BinanceApiInternalFactory instance = new BinanceApiInternalFactory();

    public static BinanceApiInternalFactory getInstance() {
        return instance;
    }

    private BinanceApiInternalFactory() {
    }

    public SyncRequestClient createSyncRequestClient(String apiKey, String secretKey, RequestOptions options) {
        RequestOptions requestOptions = new RequestOptions(options);
        RestApiRequestImpl requestImpl = new RestApiRequestImpl(apiKey, secretKey, requestOptions);
        return new SyncRequestImpl(requestImpl);
    }

    public SubscriptionClient createSubscriptionClient(SubscriptionOptions options) {
        SubscriptionOptions subscriptionOptions = new SubscriptionOptions(options);
        RequestOptions requestOptions = new RequestOptions();
        try {
            String host = new URI(options.getUri()).getHost();
            requestOptions.setUrl("https://" + host);
        } catch (Exception e) {

        }
        SubscriptionClient webSocketStreamClient = new WebSocketStreamClientImpl(subscriptionOptions);
        return webSocketStreamClient;
    }

}
