package com.project.vinance.client;

import com.project.vinance.client.exception.BinanceApiException;

/**
 * The error handler for the subscription.
 */
@FunctionalInterface
public interface SubscriptionErrorHandler {

  void onError(BinanceApiException exception);
}
