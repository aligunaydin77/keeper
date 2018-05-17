/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class PriceHolderQ3 {

    protected Map<String, PriceThroughTimeQ3> vault = new ConcurrentHashMap<>();

    /** Called when a price ‘p’ is received for an entity ‘e’ */
    public void putPrice(String e, BigDecimal p) {

        vault.compute(e, (s, priceThroughTime) -> {
            if(priceThroughTime == null) {
                priceThroughTime = new PriceThroughTimeQ3();
            }
            priceThroughTime.setPricePut(p);
            priceThroughTime.notifyRequesters();
            return priceThroughTime;
        });
    }


    /** Called to get the latest price for entity ‘e’ */
    public BigDecimal getPrice(String e) {
        return vault.computeIfAbsent(e, s -> new PriceThroughTimeQ3()).updatePriceGot();
    }

    /**
     * Called to determine if the price for entity ‘e’ has changed since the
     * last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {
        return vault.computeIfAbsent(e, s -> new PriceThroughTimeQ3()).hasPriceChanged();
    }


    /**
     * Returns the next price for entity ‘e’. If the price has changed since the
     * last call to getPrice() or waitForNextPrice(), it returns immediately
     * that price. Otherwise it blocks until the next price change for entity
     * ‘e’.
     */
    public BigDecimal waitForNextPrice(String e) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        PriceThroughTimeQ3 priceThroughTimeComputed = vault.compute(e, (s, priceTt) -> {
            if(priceTt == null) {
                PriceThroughTimeQ3 priceThroughTime = new PriceThroughTimeQ3();
                priceThroughTime.addAnotherRequester(countDownLatch);
                return priceThroughTime;
            }
            if(priceTt.hasPriceChanged()) {
                countDownLatch.countDown(); // no need to wait
            } else {
                priceTt.addAnotherRequester(countDownLatch);
            }
            return priceTt;
        });

        countDownLatch.await();
        return priceThroughTimeComputed.updatePriceGot();
    }

}
