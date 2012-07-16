package nu.ganslandt.util.commlog;

public class ToStringStringer implements Stringer {
    @Override
    public String toString(Object obj) {
        if (obj == null)
            return "null";

        return "'" + obj.toString() + "'";
    }
}
