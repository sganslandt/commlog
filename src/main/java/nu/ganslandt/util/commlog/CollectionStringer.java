package nu.ganslandt.util.commlog;

import java.util.Collection;

public class CollectionStringer extends Stringer {

    private StringerSource source;

    public CollectionStringer(StringerSource source, int maxPropertyDepth) {
        super(maxPropertyDepth);
        this.source = source;
    }

    @Override
    String doStringify(Object object, int level) {
        Collection collection;

        if (object instanceof Collection)
            collection = (Collection) object;
        else
            throw new IllegalArgumentException("CollectionStringer can only string Collections.");

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (Object item : collection) {
            builder.append(source.getStringer(item).toString(item, level));
            builder.append(", ");
        }

        if (builder.length() >= 2)
            builder.delete(builder.length() - 2, builder.length());
        builder.append("]");

        return builder.toString();
    }

    @Override
    public void addSecret(final String propertyName) {
        // Don't know about property names, nothing to do.
    }
}
