package nu.ganslandt.util.commlog;

import java.lang.reflect.Field;

public class ReflectingPropertyStringer implements Stringer {

    private StringerSource source;

    protected ReflectingPropertyStringer(StringerSource source) {
        this.source = source;
    }

    public String toString(Object obj) {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (Field f : obj.getClass().getDeclaredFields()) {
            String value;

            try {
                f.setAccessible(true);
                if (f.get(obj) != null)
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
}
