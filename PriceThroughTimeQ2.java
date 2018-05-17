/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price.q2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PriceThroughTimeQ2 {
    private BigDecimal priceGot = BigDecimal.ZERO;
    private BigDecimal pricePut = BigDecimal.ZERO;;

    public PriceThroughTimeQ2() {

    }

    public void setPricePut(BigDecimal pricePut) {
        this.pricePut = pricePut;
    }

    public BigDecimal updatePriceGot() {
        priceGot = pricePut;
        return priceGot;
    }


    public boolean hasPriceChanged() {
        return !priceGot.equals(pricePut);
    }

}
