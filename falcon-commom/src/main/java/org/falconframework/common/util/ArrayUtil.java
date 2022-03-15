package org.falconframework.common.util;

import java.util.Arrays;

public class ArrayUtil {

    public static <T> T[] concat(T[] part1, T...part2) {
        int part1Length = part1 == null ? 0 : part1.length;
        int part2Length = part2 == null ? 0 : part2.length;
        int length = part1Length + part2Length;

        T[] array = Arrays.copyOf(part1, length);
        for (int i = 0; i < part2Length; i++) {
            T obj = part2[i];
            array[length - part2Length + i] = obj;
        }
        return array;
    }

}
