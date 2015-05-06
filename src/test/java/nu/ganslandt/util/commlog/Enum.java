package nu.ganslandt.util.commlog;

public enum Enum {

    SUCCESS,
    FAILURE;

    public String value() {
        return name();
    }

    public static Enum fromValue(String v) {
        return valueOf(v);
    }

}
