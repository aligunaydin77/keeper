
package com.gs.futures.refdata.services;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class Mapper {

    public static <T> List<T> map(Function<T, T> mappingFunction, List<T> inputList) {
        return inputList.stream().map(mappingFunction).collect(toList());
    }

    @Test
    public void shouldIncrementContents() {
        List<Integer> three = Arrays.asList(1,2,3);
        Function<Integer, Integer> plusOne = integer -> integer + 1;
        List<Integer> output = map(plusOne, three);
        List<Integer> expectation = Arrays.asList(2,3,4);
        assertEquals("shouldIncrementContents ", expectation, output);
    }
}
