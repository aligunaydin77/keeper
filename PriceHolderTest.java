/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PriceHolderTest {


    PriceHolderQ3 priceHolderQ3 = new PriceHolderQ3();

    @Before
    public void init() {
        priceHolderQ3.putPrice("b", BigDecimal.valueOf(3.0));
        priceHolderQ3.putPrice("d", BigDecimal.valueOf(5.0));
    }

    @Test
    public void shouldReturnThePrice() {
        BigDecimal price = priceHolderQ3.getPrice("b");
        assertEquals(price, BigDecimal.valueOf(3.0));
    }


    @Test
    public void shouldHasChangedWorksCorrectly() throws Exception {
        priceHolderQ3.putPrice("a", BigDecimal.valueOf(1.0));
        priceHolderQ3.putPrice("a", BigDecimal.valueOf(2.0));
        assertTrue(priceHolderQ3.hasPriceChanged("a"));
        priceHolderQ3.getPrice("a");
        assertFalse(priceHolderQ3.hasPriceChanged("a"));
    }

    @Test
    public void shouldWaitForNextValue() {
        BigDecimal actual = null;
        priceHolderQ3.getPrice("d");
        changePriceInSeparateThread("d", BigDecimal.valueOf(9.0));
        try {
            actual = priceHolderQ3.waitForNextPrice("d");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(BigDecimal.valueOf(9.0), actual);

    }

    @Test
    public void shouldWaitForNextValueReturnImmediatelyIfThereIsAPriceChange() throws Exception {
        priceHolderQ3.putPrice("a", BigDecimal.valueOf(1.0));
        assertEquals(priceHolderQ3.getPrice("a"), BigDecimal.valueOf(1.0));
        priceHolderQ3.putPrice("a", BigDecimal.valueOf(2.0));
        assertEquals(priceHolderQ3.waitForNextPrice("a"), BigDecimal.valueOf(2.0));
    }

    @Test
    public void shouldWaitForTheFirstValueToArrive() {
        BigDecimal actual = null;
        changePriceInSeparateThread("c", BigDecimal.valueOf(7.0));
        try {
            actual = priceHolderQ3.waitForNextPrice("c");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(BigDecimal.valueOf(7.0), actual);

    }

    private void changePriceInSeparateThread(String e, BigDecimal p) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                priceHolderQ3.putPrice(e, p);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        thread.start();
    }

}

