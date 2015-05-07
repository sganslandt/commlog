package nu.ganslandt.util.commlog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

public class ReflectingPropertyStringer extends Stringer {

    private StringerSource source;

    private Collection<String> globalSecrets;

    protected ReflectingPropertyStringer(StringerSource source) {
        this.source = source;
        this.globalSecrets = new HashSet<>();
    }

    @Override
    String doStringify(Object obj) {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Field f : obj.getClass().getDeclaredFields()) {
            String value;

            if (Modifier.isTransient(f.getModifiers()))
                continue;

            try {
                f.setAccessible(true);
                if (globalSecrets.contains(f.getName()))
                    value = f.getName() + "=" + CommLog.SECRET_STRING;
                else if (f.get(obj) != null)
                    value = f.getName() + "=" + source.getStringer(f.get(obj)).toString(f.get(obj));
                else
                    value = f.getName() + "=" + null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                value = "###ERROR###";
            }

            sb.append(value).append(", ");
        }

        if (obj.getClass().getDeclaredFields().length > 0)
            sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void addSecret(final String propertyName) {
        globalSecrets.add(propertyName);
    }
}
