package nu.ganslandt.util.commlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CommLog implementation.
 */
public class CommLogImpl implements CommLog, StringerSource {

    private static Map<String, CommLog> loggers = new ConcurrentHashMap<String, CommLog>();

    private final Logger COMM;
    private final Logger ERROR;

    private ThreadLocal<Map<Class, Stringer>> stringerMap = new ThreadLocal<Map<Class, Stringer>>();

    private ThreadLocal<Request> request;

    private CommLogImpl(String name) {
        this.COMM = LoggerFactory.getLogger(name + "Comm");
        this.ERROR = LoggerFactory.getLogger(name + "Error");
        this.request = new ThreadLocal<Request>();
    }

    /**
     * Get a CommLog instance for the given client name.
     *
     * @param name The name of the client. Will be suffixed with Comm to create a comm log and Error to create an error
     *             log.
     * @return A CommLog instance.
     */
    public static CommLog getLog(String name) {
        CommLog log = loggers.get(name);

        if (log == null) {
            log = new CommLogImpl(name);
            loggers.put(name, log);
        }

        return log;
    }

    @Override
    public boolean hasStringerFor(Object obj) {
        return getSpecializedStringerFor(obj) != null;
    }

    @Override
    public Stringer getStringer(Object obj) {
        initStringerMap();

        Stringer stringer = getSpecializedStringerFor(obj);

        if (stringer == null)
            // fall back to basics
            stringer = new ReflectingPropertyStringer(this);

        return stringer;
    }

    private Stringer getSpecializedStringerFor(Object obj) {
        Stringer stringer;

        // try getting based on this class
        stringer = stringerMap.get().get(obj.getClass());

        // else try to find by super class
        if (stringer == null)
            for (Class c : stringerMap.get().keySet()) {
                if (c.isAssignableFrom(obj.getClass())) {
                    stringer = stringerMap.get().get(c);
                    //add a reference for this particular subclass
                    stringerMap.get().put(obj.getClass(), stringer);
                }
            }
        return stringer;
    }

    private void initStringerMap() {
        if (stringerMap.get() == null) {
            stringerMap.set(new HashMap<Class, Stringer>());
            stringerMap.get().put(Collection.class, new CollectionStringer(this));
        }
    }

    /** @inheritDoc */
    @Override
    public void request(String requestName, Object request) {
        this.request.set(new Request(requestName));
        COMM.info("Request: [{}] {}({})", new Object[]{this.request.get().getUUID(), requestName, getStringer(request).toString(request)});
    }

    /** @inheritDoc */
    @Override
    public void response() {
        Request request = this.request.get();
        String uuid;
        String requestName;

        if (request != null) {
            uuid = request.getUUID();
            requestName = request.getRequestName();
            this.request.remove();
        } else {
            uuid = "";
            requestName = "";
        }

        COMM.info("Response: [{}] {}({})", new Object[]{uuid, requestName, "void"});
    }

    /** @inheritDoc */
    @Override
    public void response(Object response) {
        Request request = this.request.get();
        String uuid;
        String requestName;

        if (request != null) {
            uuid = request.getUUID();
            requestName = request.getRequestName();
            this.request.remove();
        } else {
            uuid = "";
            requestName = "";
        }

        COMM.info("Response: [{}] {}({})", new Object[]{uuid, requestName, getStringer(response).toString(response)});
    }

    /** @inheritDoc */
    @Override
    public void error(Throwable t, boolean comm) {
        String uuid = null;
        String requestName = null;

        Request request = this.request.get();
        if (request != null) {
            uuid = request.getUUID();
            requestName = request.getRequestName();
        }

        if (comm) {
            COMM.error("Error: [{}] {}({})", new Object[]{uuid, requestName, t.toString()});
            this.request.remove();
        }

        ERROR.error("Error: [{}]", uuid, t);
    }


}
