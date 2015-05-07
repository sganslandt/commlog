package nu.ganslandt.util.commlog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class MapStringer implements Stringer {

    private final StringerSource source;

    private Collection<String> globalSecrets;

    public MapStringer(StringerSource source) {
        this.source = source;
        this.globalSecrets = new HashSet<>();
    }

    @Override
    public String toString(Object object) {

        Map map;

        if (object instanceof Map)
            map = (Map) object;
        else
            throw new IllegalArgumentException("MapStringer can only string Maps.");

        StringBuilder builder = new StringBuilder();

        builder.append("{");
        for (Object key : map.keySet()) {
            builder.append(source.getStringer(key).toString(key));
            builder.append(": ");
            if (globalSecrets.contains(key)) {
                builder.append(CommLog.SECRET_STRING);
                builder.append(", ");
            } else {
                Object entry = map.get(key);
                builder.append(source.getStringer(entry).toString(entry));
                builder.append(", ");
            }
        }

        if (builder.length() >= 2)
            builder.delete(builder.length() - 2, builder.length());
        builder.append("}");

        return builder.toString();
    }

    @Override
    public void addSecret(final String propertyName) {
        globalSecrets.add(propertyName);
    }
}
