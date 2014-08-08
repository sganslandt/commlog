package nu.ganslandt.util.commlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CommLog implementation.
 */
public class CommLogImpl implements CommLog, StringerSource {

    private static Map<String, CommLogImpl> loggers = new ConcurrentHashMap<String, CommLogImpl>();

    private final Logger COMM;
    private final Logger ERROR;

    private Map<Class, Stringer> stringerMap;
    private Map<String, Stringer> packageNameStringerMap;

    private ThreadLocal<Request> request;

    private static final Stringer DEFAULT_STRINGER = new ToStringStringer();

    private CommLogImpl(String name) {
        this.COMM = LoggerFactory.getLogger(name + "-Comm");
        this.ERROR = LoggerFactory.getLogger(name + "-Error");
        this.request = new ThreadLocal<Request>();

        stringerMap = new ConcurrentHashMap<Class, Stringer>();
        packageNameStringerMap = new ConcurrentHashMap<String, Stringer>();

    }

    /**
     * Get a CommLog instance for the given client name.
     *
     * @param name The name of the client. Will be suffixed with Comm to create a comm log and Error to create an error
     *             log.
     * @return A CommLog instance.
     */
    public static CommLog getLog(String name) {
        CommLogImpl log = loggers.get(name);

        if (log == null) {
            log = new CommLogImpl(name);
            loggers.put(name, log);
        }

        log.initStringerMap();
        return log;
    }

    @Override
    public boolean hasStringerFor(Object obj) {
        return getSpecializedStringerFor(obj) != null;
    }

    @Override
    public Stringer getStringer(Object obj) {

        if (obj == null)
            return DEFAULT_STRINGER;

        Stringer stringer = getSpecializedStringerFor(obj);
        if (stringer != null)
            return stringer;

        stringer = getSpecializedStringerForPackage(obj.getClass().getPackage().getName());
        if (stringer != null)
            return stringer;

        // fall back to basics
        return DEFAULT_STRINGER;
    }

    private Stringer getSpecializedStringerFor(Object obj) {

        if (obj.getClass().isArray())
            return new ArrayStringer(this);

        // try getting based on this class
        Stringer stringer = stringerMap.get(obj.getClass());

        // else try to find by super class
        if (stringer == null)
            for (Class c : stringerMap.keySet()) {
                if (c.isAssignableFrom(obj.getClass())) {
                    stringer = stringerMap.get(c);
                    //add a reference for this particular subclass - why?
//                    stringerMap.get().put(obj.getClass(), stringer);
                }
            }

        return stringer;
    }

    private Stringer getSpecializedStringerForPackage(String packageName) {
        return packageNameStringerMap.get(packageName);
    }

    private void initStringerMap() {
        stringerMap.put(Collection.class, new CollectionStringer(this));
        stringerMap.put(Map.class, new MapStringer(this));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void request(String requestName, Object request) {
        this.request.set(new Request(requestName));
        COMM.info("Request: [{}] {}({})", new Object[]{this.request.get().getUUID(), requestName, getStringer(request).toString(request)});
    }

    /**
     * @inheritDoc
     */
    @Override
    public void request(String requestName) {
        this.request.set(new Request(requestName));
        COMM.info("Request: [{}] {}()", new Object[]{this.request.get().getUUID(), requestName});
    }

    @Override
    public void configureStringerForClass(Class clazz, Class<? extends Stringer> stringerClass) {
        stringerMap.put(clazz, getConfiguredStringer(stringerClass));
    }

    @Override
    public void configureStringerForPackage(String packageName, Class<? extends Stringer> stringerClass) {
        packageNameStringerMap.put(packageName, getConfiguredStringer(stringerClass));
    }

    private Stringer getConfiguredStringer(Class<? extends Stringer> stringerClass) {
        Stringer stringer = null;
        for (Map.Entry<Class, Stringer> e : stringerMap.entrySet()) {
            if (e.getValue().getClass().equals(stringerClass)) {
                stringer = e.getValue();
            }
        }

        if (stringer == null) {
            Constructor<?> constructor = stringerClass.getDeclaredConstructors()[0];
            try {
                stringer = (Stringer) constructor.newInstance(this);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return stringer;
    }

    /**
     * @inheritDoc
     */
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

    /**
     * @inheritDoc
     */
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

    /**
     * @inheritDoc
     */
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
