package com.project.vinance.view;

import com.project.vinance.client.SubscriptionClient;
import com.project.vinance.view.sub.NeedToChangeList;
import com.project.vinance.view.sub.OverViewBot;

public class TestClass {
    public void test() {
        OverViewBot data = new OverViewBot(
                null, null, null, null, null,
                null, null, null, null, null
        );

        SubscriptionClient client = SubscriptionClient.create();
        client.subscribeMarkPriceEvent("btcusdt", ((event) -> {

        }), null);
    }
}
