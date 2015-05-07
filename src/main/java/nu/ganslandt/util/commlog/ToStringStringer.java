package nu.ganslandt.util.commlog;

public class ToStringStringer extends Stringer {

    @Override
    String doStringify(Object obj) {
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
