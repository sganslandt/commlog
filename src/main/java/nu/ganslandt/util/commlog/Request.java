package nu.ganslandt.util.commlog;

import java.util.UUID;

/**
 * Represents incoming request and keeps track of the request name and it's unique id.
 */
public class Request {

    private static final Request EMPTY_REQUEST = new Request("", "");

    public static Request empty() {
        return EMPTY_REQUEST;
    }

    private String requestName;
    private String uuid;

    public Request(String requestName) {
        this(requestName, UUID.randomUUID().toString());
    }

    private Request(String requestName, String uuid) {
        this.requestName = requestName;
        this.uuid = uuid;
    }

    public String getRequestName() {
        return requestName;
    }

    public String getUUID() {
        return uuid;
    }
}
