package nu.ganslandt.util.commlog;

public interface Stringer {

    String toString(Object obj);

    void addSecret(String propertyName);

}
