package nu.ganslandt.util.requestlog;

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

        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        for (Object item : collection) {
            buffer.append(source.getStringer(item).toString(item));
            buffer.append(", ");
        }

        buffer.delete(buffer.length() - 2, buffer.length());
        buffer.append("]");

        return buffer.toString();
    }
}
