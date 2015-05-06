package nu.ganslandt.util.commlog;

public class ToStringStringer extends Stringer {

    public ToStringStringer(int maxPropertyDepth) {
        super(maxPropertyDepth);
    }

    @Override
    String doStringify(Object obj, int level) {
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
