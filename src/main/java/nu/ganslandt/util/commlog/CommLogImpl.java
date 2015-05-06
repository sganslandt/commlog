package nu.ganslandt.util.commlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CommLog implementation.
 */
public class CommLogImpl implements CommLog, StringerSource {

    private static Map<String, CommLogImpl> loggers = new ConcurrentHashMap<>();

    private final Logger COMM;
    private final Logger ERROR;

    private Map<Class, Stringer> stringerMap;
    private Map<String, Stringer> packageNameStringerMap;

    private Collection<String> globalSecrets;

    private int maxPropertyDepth = 10;

    private ThreadLocal<Request> request;

    private Stringer defaultStringer;

    private CommLogImpl(String name) {
        this.COMM = LoggerFactory.getLogger(name + "-Comm");
        this.ERROR = LoggerFactory.getLogger(name + "-Error");
        this.request = new ThreadLocal<>();

        this.stringerMap = new ConcurrentHashMap<>();
        this.packageNameStringerMap = new ConcurrentHashMap<>();

        this.globalSecrets = new HashSet<>();

        this.defaultStringer = new ToStringStringer(maxPropertyDepth);
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
            return defaultStringer;

        Stringer stringer = getSpecializedStringerFor(obj);
        if (stringer != null)
            return stringer;

        stringer = getSpecializedStringerForPackage(obj.getClass().getPackage().getName());
        if (stringer != null)
            return stringer;

        // fall back to basics
        return defaultStringer;
    }

    private Stringer getSpecializedStringerFor(Object obj) {

        if (obj.getClass().isArray())
            return new ArrayStringer(this, maxPropertyDepth);

        // try getting based on this class
        Stringer stringer = stringerMap.get(obj.getClass());

        // else try to find by super class
        if (stringer == null)
            for (Class c : stringerMap.keySet()) {
                if (c.isAssignableFrom(obj.getClass())) {
                    stringer = stringerMap.get(c);
                }
            }

        return stringer;
    }

    private Stringer getSpecializedStringerForPackage(String packageName) {
        return packageNameStringerMap.get(packageName);
    }

    private void initStringerMap() {
        stringerMap.put(Collection.class, new CollectionStringer(this, maxPropertyDepth));
        stringerMap.put(Map.class, new MapStringer(this, maxPropertyDepth));
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

    @Override
    public void addSecret(final String propertyName) {
        globalSecrets.add(propertyName);

        defaultStringer.addSecret(propertyName);

        for (Stringer stringer : stringerMap.values())
            stringer.addSecret(propertyName);
        for (Stringer stringer : packageNameStringerMap.values())
            stringer.addSecret(propertyName);
    }

    @Override
    public void setMaxPropertyDepth(int maxPropertyDepth) {
        this.maxPropertyDepth = maxPropertyDepth;
        this.defaultStringer = new ToStringStringer(maxPropertyDepth);
    }

    private Stringer getConfiguredStringer(Class<? extends Stringer> stringerClass) {
        Stringer stringer = null;
        for (Map.Entry<Class, Stringer> e : stringerMap.entrySet()) {
            if (e.getValue().getClass().equals(stringerClass)) {
                stringer = e.getValue();
            }
        }

        if (stringer == null) {
            Constructor<? extends Stringer> constructor = getPreferredConstructor(stringerClass);
            try {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 2
                        && parameterTypes[0].equals(StringerSource.class)
                        && parameterTypes[1].equals(int.class))
                    stringer = constructor.newInstance(this, maxPropertyDepth);
                else if (parameterTypes.length == 1)
                    stringer = constructor.newInstance(maxPropertyDepth);
                else if (parameterTypes.length == 0)
                    stringer = constructor.newInstance();
                else
                    throw new RuntimeException("Don't know how to construct a " + stringerClass.getName());

                for (String secret : globalSecrets)
                    stringer.addSecret(secret);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        return stringer;
    }

    private Constructor<? extends Stringer> getPreferredConstructor(final Class<? extends Stringer> stringerClass) {
        Constructor<? extends Stringer> preferredConstructor = null;
        try {
            preferredConstructor = stringerClass.getDeclaredConstructor(StringerSource.class, int.class);
        } catch (NoSuchMethodException ignored) {
            // moving along
            try {
                preferredConstructor = stringerClass.getDeclaredConstructor();
            } catch (NoSuchMethodException ignored2) {
                // moving along
            }
        }

        if (preferredConstructor == null)
            throw new RuntimeException("Found neither a no args constructor or one taking a StringerSource.");
        else
            return preferredConstructor;
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
    public <T> T response(T response) {
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

        return response;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T> T error(T response) {
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

        COMM.error("Error: [{}] {}({})", new Object[]{uuid, requestName, getStringer(response).toString(response)});

        return response;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T extends Throwable> T error(T t, boolean comm) {
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

        return t;
    }

    /**
     * @inheritDoc
     */
    @Override
    public <R, T extends Throwable> R error(T t, R response) {
        String uuid = null;
        String requestName = null;

        Request request = this.request.get();
        if (request != null) {
            uuid = request.getUUID();
            requestName = request.getRequestName();
            this.request.remove();
        } else {
            uuid = "";
            requestName = "";
        }

        COMM.error("Error: [{}] {}({})", new Object[]{uuid, requestName, getStringer(response).toString(response)});

        ERROR.error("Error: [{}]", uuid, t);

        return response;
    }
}
