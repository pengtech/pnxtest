/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.core.assertions;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;


public class PnxAssert {

    private PnxAssert() {
        //prevent instantiated
    }

    //=========
    //boolean
    //=========
    public static BooleanAssert assertThat(boolean actual) {
        return new BooleanAssert(actual);
    }
    public static BooleanAssert assertThat(Boolean actual) {
        return new BooleanAssert(actual);
    }

    //=========
    //byte
    //=========
    public static ByteAssert assertThat(byte actual) {
        return new ByteAssert(actual);
    }
    public static ByteAssert assertThat(Byte actual) {
        return new ByteAssert(actual);
    }
    public static ByteArrayAssert assertThat(byte[] actual) {
        return new ByteArrayAssert(primitiveArrayToObject(actual));
    }
    public static ByteArrayAssert assertThat(Byte[] actual) {
        return new ByteArrayAssert(actual);
    }

    //=========
    //short
    //=========
    public static ShortAssert assertThat(short actual) {
        return new ShortAssert(actual);
    }
    public static ShortAssert assertThat(Short actual) {
        return new ShortAssert(actual);
    }
    public static ShortArrayAssert assertThat(short[] actual) {
        return new ShortArrayAssert(primitiveArrayToObject(actual));
    }
    public static ShortArrayAssert assertThat(Short[] actual) {
        return new ShortArrayAssert(actual);
    }

    //=========
    //int
    //=========
    public static IntegerAssert assertThat(int actual) {
        return new IntegerAssert(actual);
    }

    public static IntegerAssert assertThat(Integer actual) {
        return new IntegerAssert(actual);
    }

    public static IntegerArrayAssert assertThat(int[] actual) {
        return new IntegerArrayAssert(primitiveArrayToObject(actual));
    }

    public static IntegerArrayAssert assertThat(Integer[] actual) {
        return new IntegerArrayAssert(actual);
    }

    //=========
    //long
    //=========
    public static LongAssert assertThat(long actual) {
        return new LongAssert(actual);
    }
    public static LongAssert assertThat(Long actual) {
        return new LongAssert(actual);
    }
    public static LongArrayAssert assertThat(long[] actual) {
        return new LongArrayAssert(primitiveArrayToObject(actual));
    }
    public static LongArrayAssert assertThat(Long[] actual) {
        return new LongArrayAssert(actual);
    }

    //=========
    //float
    //=========
    public static FloatAssert assertThat(float actual) {
        return new FloatAssert(actual);
    }
    public static FloatAssert assertThat(Float actual) {
        return new FloatAssert(actual);
    }
    public static FloatArrayAssert assertThat(float[] actual) {
        return new FloatArrayAssert(primitiveArrayToObject(actual));
    }
    public static FloatArrayAssert assertThat(Float[] actual) {
        return new FloatArrayAssert(actual);
    }

    //=========
    //double
    //=========
    public static DoubleAssert assertThat(double actual) {
        return new DoubleAssert(actual);
    }
    public static DoubleAssert assertThat(Double actual) {
        return new DoubleAssert(actual);
    }
    public static DoubleArrayAssert assertThat(double[] actual) {
        return new DoubleArrayAssert(primitiveArrayToObject(actual));
    }
    public static DoubleArrayAssert assertThat(Double[] actual) {
        return new DoubleArrayAssert(actual);
    }

    //=========
    //char
    //=========
    public static CharAssert assertThat(Character actual) {
        return new CharAssert(actual);
    }
    public static CharAssert assertThat(char actual) {
        return new CharAssert(actual);
    }
    public static CharArrayAssert assertThat(char[] actual) {
        return new CharArrayAssert(primitiveArrayToObject(actual));
    }
    public static CharArrayAssert assertThat(Character[] actual) {
        return new CharArrayAssert(actual);
    }

    //=========
    //String
    //=========
    public static StringAssert assertThat(String actual) {
        return new StringAssert(actual);
    }
    public static StringArrayAssert assertThat(String[] actual) {
        return new StringArrayAssert(actual);
    }

    //=========
    //Object
    //=========
    public static ObjectAssert assertThat(Object actual) {
        return new ObjectAssert(actual);
    }
    public static ObjectArrayAssert assertThat(Object[] actual) {
        return new ObjectArrayAssert(actual);
    }

    //=========
    //Map
    //=========
    public static <K, V> MapAssert assertThat(Map<K, V> actual) {
        return new MapAssert(actual);
    }

    //=========
    //File
    //=========
    public static FileAssert assertThat(File actual) {
        return new FileAssert(actual);
    }

    //=========
    //BigDecimal
    //=========
    public static BigDecimalAssert assertThat(BigDecimal actual) {
        return new BigDecimalAssert(actual);
    }

    //=========
    //Collection
    //=========
    public static CollectionAssert assertThat(Collection<?> actual) {
        return new CollectionAssert(actual);
    }

    public static ListAssert assertThat(List<?> actual) {
        return new ListAssert(actual);
    }

    public static <E> SetAssert assertThat(Set<E> actual) {
        return new SetAssert(actual);
    }


    private static Byte[] primitiveArrayToObject(byte[] bytes){
        if(bytes == null) return null;

        Byte[] objects = new Byte[bytes.length];
        Arrays.setAll(objects, n -> bytes[n]);
        return objects;
    }

    private static Short[] primitiveArrayToObject(short[] shorts){
        if(shorts == null) return null;

        Short[] objects = new Short[shorts.length];
        Arrays.setAll(objects, n -> shorts[n]);
        return objects;
    }

    private static Integer[] primitiveArrayToObject(int[] ints){
        //Integer[] actualObject = Arrays.stream( ints ).boxed().toArray( Integer[]::new );
        if(ints == null) return null;

        Integer[] objects = new Integer[ints.length];
        Arrays.setAll(objects, n -> ints[n]);
        return objects;
    }

    private static Long[] primitiveArrayToObject(long[] longs){
        if(longs == null) return null;

        Long[] objects = new Long[longs.length];
        Arrays.setAll(objects, n -> longs[n]);
        return objects;
    }

    private static Float[] primitiveArrayToObject(float[] floats){
        if(floats == null) return null;

        Float[] objects = new Float[floats.length];
        Arrays.setAll(objects, n -> floats[n]);
        return objects;
    }

    private static Double[] primitiveArrayToObject(double[] doubles){
        if(doubles == null) return null;

        Double[] objects = new Double[doubles.length];
        Arrays.setAll(objects, n -> doubles[n]);
        return objects;
    }

    private static Character[] primitiveArrayToObject(char[] chars){
        if(chars == null) return null;

        Character[] objects = new Character[chars.length];
        Arrays.setAll(objects, n -> chars[n]);
        return objects;
    }


}
