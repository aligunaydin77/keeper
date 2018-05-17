/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PriceThroughTime {
    private BigDecimal priceGot = BigDecimal.ZERO;
    private BigDecimal pricePut = BigDecimal.ZERO;;
    private List<CountDownLatch> requestCounter;

    public PriceThroughTime() {
        requestCounter = new ArrayList<>();
    }

    public void setPricePut(BigDecimal pricePut) {
        this.pricePut = pricePut;
    }


    public void notifyRequesters() {
        requestCounter.forEach(CountDownLatch::countDown);
        requestCounter.removeIf(countDownLatch -> true);
    }

    public BigDecimal updatePriceGot() {
        priceGot = pricePut;
        return priceGot;
    }


    public boolean hasPriceChanged() {
        return !priceGot.equals(pricePut);
    }


    public void addAnotherRequester(CountDownLatch countDownLatch) {
        requestCounter.add(countDownLatch);
    }
}
