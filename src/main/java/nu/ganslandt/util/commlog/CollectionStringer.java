package nu.ganslandt.util.commlog;

import java.util.Collection;

public class CollectionStringer implements Stringer {

    private StringerSource source;

    public CollectionStringer(StringerSource source) {
        this.source = source;
    }

    @Override
    public String toString(Object object) {
        Collection collection;

        if (object instanceof Collection)
            collection = (Collection) object;
        else
            throw new IllegalArgumentException("CollectionStringer can only string Collections.");

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (Object item : collection) {
            builder.append(source.getStringer(item).toString(item));
            builder.append(", ");
        }

        if (builder.length() >= 2)
            builder.delete(builder.length() - 2, builder.length());
        builder.append("]");

        return builder.toString();
    }
}
