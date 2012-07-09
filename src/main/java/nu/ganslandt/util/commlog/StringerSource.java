package nu.ganslandt.util.commlog;

public interface StringerSource {

    boolean hasStringerFor(Object obj);

    Stringer getStringer(Object obj);

}
