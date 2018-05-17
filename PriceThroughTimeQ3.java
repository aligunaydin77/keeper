
package com.gs.futures.refdata.services.prime.price;

import com.gs.futures.refdata.services.prime.price.q2.PriceThroughTimeQ2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PriceThroughTimeQ3 extends PriceThroughTimeQ2 {
    private List<CountDownLatch> requestCounter;

    public PriceThroughTimeQ3() {
        requestCounter = new ArrayList<>();
    }

    public void notifyRequesters() {
        requestCounter.forEach(CountDownLatch::countDown);
        requestCounter.removeIf(countDownLatch -> true);
    }

    public void addAnotherRequester(CountDownLatch countDownLatch) {
        requestCounter.add(countDownLatch);
    }
}
