package nu.ganslandt.util.commlog;

public abstract class Stringer {

    private final int maxPropertyDepth;

    Stringer(int maxPropertyDepth) {
        this.maxPropertyDepth = maxPropertyDepth;
    }

    String toString(Object obj) {
        return toString(obj, 0);
    }

    String toString(Object obj, int level) {
        if (level > maxPropertyDepth)
            return "ERR_TO_DEEP_NESTING";
        else
            return doStringify(obj, level + 1);
    }

    abstract String doStringify(Object obj, int level);

    abstract void addSecret(String propertyName);

}
