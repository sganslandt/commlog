package nu.ganslandt.util.commlog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

public class ReflectingPropertyStringer extends Stringer {

    private StringerSource source;

    private Collection<String> globalSecrets;

    protected ReflectingPropertyStringer(StringerSource source, int maxPropertyDepth) {
        super(maxPropertyDepth);
        this.source = source;
        this.globalSecrets = new HashSet<>();
    }

    @Override
    String doStringify(Object obj, int level) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        Class<?> clazz = obj.getClass();
        while (!clazz.equals(Object.class)) {

            for (Field f : clazz.getDeclaredFields()) {
                String value;

                if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                try {
                    f.setAccessible(true);
                    if (globalSecrets.contains(f.getName()))
                        value = f.getName() + "=" + CommLog.SECRET_STRING;
                    else if (f.get(obj) != null)
                        value = f.getName() + "=" + source.getStringer(f.get(obj)).toString(f.get(obj), level);
                    else
                        value = f.getName() + "=" + null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    value = "###ERROR###";
                }

                sb.append(value).append(", ");

            }
            clazz = clazz.getSuperclass();
        }

        if (sb.length() > 1)
            sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void addSecret(final String propertyName) {
        globalSecrets.add(propertyName);
    }
}
