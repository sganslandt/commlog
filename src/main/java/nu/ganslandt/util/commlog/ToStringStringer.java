package nu.ganslandt.util.commlog;

public class ToStringStringer implements Stringer {

    @Override
    public String toString(Object obj) {
        if (obj == null)
            return "null";
        else if (obj instanceof String)
            return "'" + obj.toString() + "'";

        return obj.toString();
    }

    @Override
    public void addSecret(final String propertyName) {
        // Don't know about property names, nothing to do.
    }
}
