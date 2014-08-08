package nu.ganslandt.util.commlog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArrayStringer implements Stringer {

    private final CollectionStringer collectionStringer;

    public ArrayStringer(StringerSource source) {
        collectionStringer = new CollectionStringer(source);
    }

    @Override
    public String toString(Object obj) {
        if (obj.getClass().equals(char[].class))
            return collectionStringer.toString(buildCollection((char[]) obj));
        if (obj.getClass().equals(byte[].class))
            return collectionStringer.toString(buildCollection((byte[]) obj));
        if (obj.getClass().equals(short[].class))
            return collectionStringer.toString(buildCollection((short[]) obj));
        if (obj.getClass().equals(int[].class))
            return collectionStringer.toString(buildCollection((int[]) obj));
        if (obj.getClass().equals(long[].class))
            return collectionStringer.toString(buildCollection((long[]) obj));
        if (obj.getClass().equals(float[].class))
            return collectionStringer.toString(buildCollection((float[]) obj));
        if (obj.getClass().equals(double[].class))
            return collectionStringer.toString(buildCollection((double[]) obj));
        if (obj.getClass().equals(boolean[].class))
            return collectionStringer.toString(buildCollection((boolean[]) obj));

        return collectionStringer.toString(Arrays.asList((Object[]) obj));
    }

    private Collection buildCollection(char[] array) {
        Collection<Character> characterCollection = new ArrayList<>(array.length);
        for (char e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(byte[] array) {
        Collection<Byte> characterCollection = new ArrayList<>(array.length);
        for (byte e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(short[] array) {
        Collection<Short> characterCollection = new ArrayList<>(array.length);
        for (Short e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(int[] array) {
        Collection<Integer> characterCollection = new ArrayList<>(array.length);
        for (int e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(long[] array) {
        Collection<Long> characterCollection = new ArrayList<>(array.length);
        for (long e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(float[] array) {
        Collection<Float> characterCollection = new ArrayList<>(array.length);
        for (float e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(double[] array) {
        Collection<Double> characterCollection = new ArrayList<>(array.length);
        for (double e : array)
            characterCollection.add(e);

        return characterCollection;
    }

    private Collection buildCollection(boolean[] array) {
        Collection<Boolean> characterCollection = new ArrayList<>(array.length);
        for (boolean e : array)
            characterCollection.add(e);

        return characterCollection;
    }
}