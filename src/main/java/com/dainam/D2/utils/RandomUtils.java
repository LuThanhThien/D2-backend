package com.dainam.D2.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomUtils {

    static public <T> T randomChoice(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    static public int randomInt(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue + 1);
    }

}
