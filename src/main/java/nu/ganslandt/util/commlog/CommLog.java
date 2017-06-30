package nu.ganslandt.util.commlog;

/**
 * Communication logger that logs incoming requests and maps them to their respective responses or errors.
 * <p/>
 * All requests should be followed by exactly one response or comm error and each response or comm error should be
 * preceded by exactly one request.
 */
public interface CommLog {

    static final String SECRET_STRING = "********";

    /**
     * Logs an empty request with a unique identifier and name.
     *
     * @param requestName The name of the request.
     */
    void request(String requestName);

    /**
     * Logs a request with a unique identifier, name and request object.
     *
     * @param requestName The name of the request.
     * @param request     The request object.
     */
    void request(String requestName, Object request);

    /**
     * Logs an informational message using the identifier of the preceding request
     *
     * @param message The message to log
     */
    void info(String message);

    /**
     * Logs an empty response and maps it to a preceding request.
     */
    void response();

    /**
     * Logs a response with a response object.
     *
     * @param response The response object.
     */
    <T> T response(T response);

    /**
     * Logs a response message as an error.
     *
     * @param response    The error message that was returned.
     */
    <T> T error(T response);

    /**
     * Logs an error. This will result a stack trace in the error log and an error entry mapped to the preceding request
     * in the comm log, if comm is true,
     *
     * @param t    The exception that occurred.
     * @param comm Whether or not this error is a part of the communication. If true, it will produce an entry in the
     *             comm log, otherwise only the stack trace will be logged in the error log.
     */
    <T extends Throwable> T error(T t, boolean comm);

    /**
     * Logs a response and an error. This will result a stack trace in the error log and an error entry mapped to the preceding request
     * in the comm log with the response.
     *
     * @param t         The exception that occurred.
     * @param response  The error message that was returned.
     */
    <R, T extends Throwable> R error(T t, R response);

    void configureStringerForClass(Class clazz, Class<? extends Stringer> stringerClass);

    void configureStringerForPackage(String packageName, Class<? extends Stringer> stringerClass);

    /**
     * Mark a property secret. Secrets will be masked and printed as ********.
     * <p/>
     * This request will be propagated to all existing and new Stringers and it's ultimately up to the Stringers to
     * properly handle secrets.
     *
     * @param propertyName -
     */
    void addSecret(String propertyName);

    /**
     * Set a threshold for how deep within objects the Stringers should look for additional properties to include.
     *
     * After this threshold has been reach, a dummy value will be returned instead of the actual value.
     *
     * This is to prevent StackOverflows when Stringing objects that either reference themselves or are just to
     * big graphs to be able to conveniently handle.
     *
     * @param maxPropertyDepth -
     */
    void setMaxPropertyDepth(int maxPropertyDepth);
}
