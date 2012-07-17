package nu.ganslandt.util.commlog;

import java.util.Arrays;

public class ArrayStringer implements Stringer {

    private final CollectionStringer collectionStringer;

    public ArrayStringer(StringerSource source) {
        collectionStringer = new CollectionStringer(source);
    }

    @Override
    public String toString(Object obj) {
        return collectionStringer.toString(Arrays.asList((Object[]) obj));
    }
}