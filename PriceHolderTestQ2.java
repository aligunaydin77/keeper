/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price.q2;

import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PriceHolderTestQ2 {


    PriceHolderQ2 priceHolderQ2 = new PriceHolderQ2();


    @Test
    public void shouldHasChangedWorksCorrectly() throws Exception {
        priceHolderQ2.putPrice("a", new BigDecimal(10));
        assertEquals(priceHolderQ2.getPrice("a"), BigDecimal.TEN);
        priceHolderQ2.putPrice("a", new BigDecimal(12));
        assertTrue(priceHolderQ2.hasPriceChanged("a"));
        priceHolderQ2.putPrice("b", new BigDecimal(2));
        priceHolderQ2.putPrice("a", new BigDecimal(11));
        assertEquals(priceHolderQ2.getPrice("a"), new BigDecimal(11));
        assertEquals(priceHolderQ2.getPrice("a"), new BigDecimal(11));
        assertEquals(priceHolderQ2.getPrice("b"), new BigDecimal(2));
    }

}

