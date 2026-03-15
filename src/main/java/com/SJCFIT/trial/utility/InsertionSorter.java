package com.SJCFIT.trial.utility;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class InsertionSorter {

    public static <T> void insertionSort(List<T> list, Comparator<T> comparator){
        IntStream.range(1, list.size()).forEach(i ->{
                    T key = list.get(i);
                    int j = i-1;

                    while(j>=0 && comparator.compare(list.get(j), key) > 0){
                        list.set(j+1, list.get(j));
                        j--;
                    }
                    list.set(j+1, key);
                }
        );
    }
}
