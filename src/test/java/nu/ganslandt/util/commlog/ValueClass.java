package nu.ganslandt.util.commlog;

class ValueClass {
    private String value1;
    private Integer value2;
    private ValueClass optionalNestedValue;

    public ValueClass(String value1, Integer value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public ValueClass(String value1, Integer value2, ValueClass nestedValue) {
        this.value1 = value1;
        this.value2 = value2;
        this.optionalNestedValue = nestedValue;
    }
}
