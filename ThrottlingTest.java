
package com.gs.futures.refdata.services.prime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ThrottlingTest {

    private static final String MESSAGE = "hello";
    Throttling throttling = new Throttling(3, 5000);

    @Mock
    Consumer<String> mockConsumer;

    @Test
    public void shouldProcessFairNumberOfFunctions() throws Exception {
        Thread thread = new Thread(() ->
                Stream.of(1, 2, 3, 4, 5).forEach(integer -> {
                    throttling.process(mockConsumer, MESSAGE);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
        thread.start();
        Thread.sleep(5000);
        verify(mockConsumer, times(3)).accept(MESSAGE);
        thread.join();
        verify(mockConsumer, times(5)).accept(MESSAGE);
    }
}
