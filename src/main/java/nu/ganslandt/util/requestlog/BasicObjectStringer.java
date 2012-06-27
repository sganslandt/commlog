package nu.ganslandt.util.requestlog;

import java.lang.reflect.Field;

public class BasicObjectStringer implements Stringer {

    private StringerSource source;

    protected BasicObjectStringer(StringerSource source) {
        this.source = source;
    }

    public String toString(Object obj) {

        StringBuffer sb = new StringBuffer();
        Package thisPackage = obj.getClass().getPackage();

        sb.append("{");

        for (Field f : obj.getClass().getDeclaredFields()) {
            String value;

            try {
                f.setAccessible(true);
                if (f.get(obj) != null && (thisPackage.equals(f.get(obj).getClass().getPackage()) || source.hasStringerFor(f.get(obj))))
                    value = f.getName() + "=" + source.getStringer(f.get(obj)).toString(f.get(obj));
                else
                    value = f.getName() + "=" + f.get(obj);
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
