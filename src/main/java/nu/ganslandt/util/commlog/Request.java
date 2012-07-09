package nu.ganslandt.util.commlog;

import java.util.UUID;

/**
 * Represents incoming request and keeps track of the request name and it's unique id.
 */
public class Request {

    private String requestName;
    private String uuid;

    public Request(String requestName) {
        this.requestName = requestName;
        this.uuid = UUID.randomUUID().toString();
    }

    public String getRequestName() {
        return requestName;
    }

    public String getUUID() {
        return uuid;
    }

}
