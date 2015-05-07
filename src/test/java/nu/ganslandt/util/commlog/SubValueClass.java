package nu.ganslandt.util.commlog;

public class SubValueClass extends ValueClass {
    private final Integer value3;
    public SubValueClass(String value1, Integer value2, Integer value3) {
        super(value1, value2);
        this.value3 = value3;
    }
}
