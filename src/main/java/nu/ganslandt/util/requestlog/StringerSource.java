package nu.ganslandt.util.requestlog;

public interface StringerSource {

    boolean hasStringerFor(Object obj);

    Stringer getStringer(Object obj);

}
