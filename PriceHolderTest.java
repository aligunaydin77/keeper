/*
 * Copyright 2018 Goldman Sachs.
 */
package com.gs.futures.refdata.services.prime.price;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PriceHolderTest {


//        PriceHolder priceHolder = new PriceHolder();
           ModernPriceHolder priceHolder = new ModernPriceHolder();

        @Before
        public void init() {
            priceHolder.putPrice(NUMBER.ZERO.toString(), NUMBERS[0]);
            priceHolder.putPrice(NUMBER.ONE.toString(), NUMBERS[1]);
        }

        @Test
        public void simplePutGetTest() {
            BigDecimal price = priceHolder.getPrice(NUMBER.ZERO.toString());
            assertEquals(price, NUMBERS[0]);
        }

        @Test
        public void waitForNextValueTest() {
            BigDecimal expected = null;
            changePriceInSeparateThread(NUMBER.ONE.toString());
            try {
                expected = priceHolder.waitForNextPrice(NUMBER.ONE.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("Received: " + expected + " is " + priceHolder.getPrice(NUMBER.ONE.toString()));
            assertEquals(expected, NUMBERS[2]);

        }

    @Test
    public void shouldHasChangedWorksCorrectly() throws Exception {
        priceHolder.putPrice("a", BigDecimal.valueOf(1.0));
        priceHolder.putPrice("a", BigDecimal.valueOf(2.0));
        assertEquals(priceHolder.hasPriceChanged("a"), true);
    }

    @Test
    public void shouldWaitForNextValueReturnsImmediatelyIfThereIsAPriceChange() throws Exception {
        priceHolder.putPrice("a", BigDecimal.valueOf(1.0));
        assertEquals(priceHolder.getPrice("a"),BigDecimal.valueOf(1.0)) ;
        priceHolder.putPrice("a", BigDecimal.valueOf(2.0));
        assertEquals(priceHolder.waitForNextPrice("a"), BigDecimal.valueOf(2.0));
    }

    @Test
    public void shouldWaitForTheFirstValueToArrive() {
        BigDecimal expected = null;
        changePriceInSeparateThread("c");
        try {
            expected = priceHolder.waitForNextPrice("c");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Received: " + expected + " is " + priceHolder.getPrice(NUMBER.ONE.toString()));
        assertEquals(expected, NUMBERS[2]);

    }

    private void changePriceInSeparateThread(String e) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    priceHolder.putPrice(e, NUMBERS[2]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            System.out.println("Looking for price change for " + NUMBER.ONE.toString() + ":"
                    + priceHolder.getPrice(NUMBER.ONE.toString()));

            thread.start();
        }

        final static BigDecimal[] NUMBERS = {
                new BigDecimal(0.0), new BigDecimal(1.0), new BigDecimal(2.0),
                new BigDecimal(3.0), new BigDecimal(4.0), new BigDecimal(5.0) };
        enum NUMBER {
            ZERO, ONE, TWO, THREE, FOUR, FIVE
        }
    }

