package nu.ganslandt.util.commlog;

public abstract class Stringer {

    String toString(Object obj) {
        try {
            return doStringify(obj);
        } catch (Throwable t) {
            return "Failed to Stringify an instance of " + obj.getClass() + " (" + t.toString() + ")";
        }
    }

    abstract String doStringify(Object obj);

    abstract void addSecret(String propertyName);

}
