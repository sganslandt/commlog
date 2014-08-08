package nu.ganslandt.util.commlog;

class ValueClassWithSecret {
    private String value1;
    private Integer value2;
    private ValueClassWithSecret optionalNestedValue;
    private String secretProperty;

    public ValueClassWithSecret(String value1, Integer value2, String secretValue) {
        this.value1 = value1;
        this.value2 = value2;
        this.secretProperty = secretValue;
    }

    public ValueClassWithSecret(String value1, Integer value2, ValueClassWithSecret nestedValue, String secretValue) {
        this.value1 = value1;
        this.value2 = value2;
        this.optionalNestedValue = nestedValue;
        this.secretProperty = secretValue;
    }
}
