/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price.q2;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PriceHolderQ2 {

    protected Map<String, PriceThroughTimeQ2> vault = new ConcurrentHashMap<>();

    /** Called when a price ‘p’ is received for an entity ‘e’ */
    public void putPrice(String e, BigDecimal p) {

        vault.compute(e, (s, priceThroughTime) -> {
            if(priceThroughTime == null) {
                priceThroughTime = new PriceThroughTimeQ2();
            }
            priceThroughTime.setPricePut(p);
            return priceThroughTime;
        });
    }


    /** Called to get the latest price for entity ‘e’ */
    public BigDecimal getPrice(String e) {
        return vault.computeIfAbsent(e, s -> new PriceThroughTimeQ2()).updatePriceGot();
    }

    /**
     * Called to determine if the price for entity ‘e’ has changed since the
     * last call to getPrice(e).
     */
    public boolean hasPriceChanged(String e) {
        return vault.computeIfAbsent(e, s -> new PriceThroughTimeQ2()).hasPriceChanged();
    }



}
