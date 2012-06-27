package nu.ganslandt.util.requestlog;

/**
 * Communication logger that logs incoming requests and maps them to their respective responses or errors.
 * <p/>
 * All requests should be followed by exactly one response or comm error and each response or comm error should be
 * preceded by exactly one request.
 */
public interface CommLog {

    /**
     * Logs a request with a unique identifier, name and request object.
     *
     * @param requestName The name of the request.
     * @param request     The request object.
     */
    void request(String requestName, Object request);

    /** Logs an empty response and maps it to a preceding request. */
    void response();

    /**
     * Logs a response with a response object.
     *
     * @param response The response object.
     */
    void response(Object response);

    /**
     * Logs an error. This will result a stacktrace in the error log and an error entry mapped to the preceding request
     * in the comm log, if comm is true,
     *
     * @param t    The exception that occurred.
     * @param comm Whether or not this error is a part of the communication. If true, it will produce an entry in the
     *             comm log, otherwise only the stacktrace will be logged in the error log.
     */
    void error(Throwable t, boolean comm);
}
