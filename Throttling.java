
package com.gs.futures.refdata.services.prime;

import com.google.common.primitives.Ints;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Throttling {

    private final int numberOfMessages;
    private final long inMiliseconds;
    private DelayQueue<MyDelayedItem> delayQueue;

    public Throttling(int numberOfMessages, long inMiliseconds) {
        this.numberOfMessages = numberOfMessages;
        this.inMiliseconds = inMiliseconds;

        delayQueue = new DelayQueue<>();
        IntStream.rangeClosed(1, numberOfMessages).forEach(i -> delayQueue.add(MyDelayedItem.getOne(0)));
    }

    public void process(Consumer<String> consumer, String message) {
        try {
            delayQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.accept(message);
        delayQueue.add(MyDelayedItem.getOne(inMiliseconds));
    }

}

class MyDelayedItem implements Delayed {

    private long delayInMilliseconds;
    private long startTime;

    public MyDelayedItem(long delayInMilliseconds) {
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
        this.delayInMilliseconds = delayInMilliseconds;
    }

    public static MyDelayedItem getOne(long delayInMilliSecond) {
        return new MyDelayedItem(delayInMilliSecond);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime -  System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        //return 0;
        return Ints.saturatedCast(
                this.startTime - ((MyDelayedItem) o).startTime);
    }

    @Override
    public String toString() {
        return "delayed: " + delayInMilliseconds;
    }
}
