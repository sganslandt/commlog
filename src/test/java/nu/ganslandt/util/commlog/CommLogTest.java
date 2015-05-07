package nu.ganslandt.util.commlog;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class CommLogTest {

    private CommLogImpl commLog;

    @Before
    public void setup() {
        commLog = (CommLogImpl) CommLogImpl.getLog("TestLog");
        commLog.addSecret("secretProperty"); // If the secret doesn't show up anywhere, nothing should change
    }

    @Test
    public void testToStringStringer_stringsStrings() {
        String string = "abc22@ganslandt.nu";

        String s = commLog.getStringer(string).toString(string);

        assertEquals("'" + string + "'", s);
    }

    @Test
    public void testToStringStringer_stringsIntegers() {
        Integer integer = Integer.valueOf(10);

        String s = commLog.getStringer(integer).toString(integer);

        assertEquals("10", s);
    }

    @Test
    public void testCollectionStringer_stringsEmptyCollections() {
        LinkedList<String> linkedList = new LinkedList<String>();

        String s = commLog.getStringer(linkedList).toString(linkedList);

        assertEquals("[]", s);
    }

    @Test
    public void testCollectionStringer_stringsCollectionsWithContent() {
        LinkedList<String> linkedList = new LinkedList<String>();
        linkedList.add("abc");
        linkedList.add("123");

        String s = commLog.getStringer(linkedList).toString(linkedList);

        assertEquals("['abc', '123']", s);
    }

    @Test
    public void testMapStringer_stringsEmptyMaps() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();

        String s = commLog.getStringer(linkedHashMap).toString(linkedHashMap);

        assertEquals("{}", s);
    }

    @Test
    public void testMapStringer_stringMapsWithContent() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("result_code", "0");
        linkedHashMap.put("result_message", "Failed: Nothing is returned.");
        linkedHashMap.put("result_output", "json");

        String s = commLog.getStringer(linkedHashMap).toString(linkedHashMap);

        assertEquals("{'result_code': '0', 'result_message': 'Failed: Nothing is returned.', 'result_output': 'json'}", s);
    }

    @Test
    public void testReflectingPropertyStringer_getsUsedWhenConfiguredViaClass() {
        commLog.configureStringerForClass(ValueClass.class, ReflectingPropertyStringer.class);
        ValueClass value = new ValueClass("abc", Integer.valueOf(123), new ValueClass("def", Integer.valueOf(456)));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2=123, optionalNestedValue={value1='def', value2=456, optionalNestedValue=null}}", s);
    }

    @Test
    public void testReflectingPropertyStringer_logsSuperClassAttributesWhenSubClassIsLogs() {
        commLog.configureStringerForClass(SubValueClass.class, ReflectingPropertyStringer.class);
        SubValueClass value = new SubValueClass("abc", Integer.valueOf(123), Integer.valueOf(11));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value3=11, value1='abc', value2=123, optionalNestedValue=null}", s);
    }


    @Test
    public void testReflectingPropertyStringer_getsUsedWhenConfiguredViaPackage() {
        commLog.configureStringerForPackage("nu.ganslandt.util.commlog", ReflectingPropertyStringer.class);
        ValueClass value = new ValueClass("abc", Integer.valueOf(123), new ValueClass("def", Integer.valueOf(456)));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2=123, optionalNestedValue={value1='def', value2=456, optionalNestedValue=null}}", s);
    }

    @Test
    public void testReflectingPropertyStringer_canLogEnums(){
        commLog.configureStringerForPackage("nu.ganslandt.util.commlog", ReflectingPropertyStringer.class);

        String s = commLog.getStringer(Enum.FAILURE).toString(Enum.FAILURE);

        assertEquals("FAILURE", s);
    }

    @Test
    public void testReflectingPropertyStringer_withEnum() {
        commLog.configureStringerForClass(TestClassWithEnum.class, ReflectingPropertyStringer.class);
        commLog.configureStringerForClass(Enum.class, ReflectingPropertyStringer.class);

        TestClassWithEnum value = new TestClassWithEnum();
        value.setTheEnum(Enum.SUCCESS);

        String s = commLog.getStringer(value).toString(value);
        assertEquals("{theEnum=SUCCESS}", s);
    }

    @Test
    public void testNullStringer() {
        String s = commLog.getStringer(null).toString(null);

        assertEquals("null", s);
    }

    @Test
    public void testMapString_withNullEntry() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("value", null);

        String s = commLog.getStringer(linkedHashMap).toString(linkedHashMap);

        assertEquals("{'value': null}", s);
    }

    @Test
    public void testMapStringer_withMapContent() {
        LinkedHashMap<String, LinkedHashMap<String, String>> linkedHashMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        linkedHashMap.put("value", new LinkedHashMap<String, String>());

        String s = commLog.getStringer(linkedHashMap).toString(linkedHashMap);

        assertEquals("{'value': {}}", s);
    }

    @Test
    public void testArrayStringer_withEmptyArray() {
        Object[] objects = new Object[0];

        String s = commLog.getStringer(objects).toString(objects);

        assertEquals("[]", s);
    }

    @Test
    public void testArrayStringer_withStringArrayAndNullValues() {
        String[] objects = new String[3];
        objects[0] = "abc";

        String s = commLog.getStringer(objects).toString(objects);

        assertEquals("['abc', null, null]", s);
    }

    @Test
    public void testArrayStringer_withVariousContent() {
        Object[] objects = new Object[3];
        objects[0] = "abc";
        objects[1] = 123;
        objects[2] = 1.23;

        String s = commLog.getStringer(objects).toString(objects);

        assertEquals("['abc', 123, 1.23]", s);
    }

    @Test
    public void testArrayStringer_withObjectsHavingConfiguredStringer() {
        Object[] objects = new Object[2];
        objects[0] = "abc";
        objects[1] = new ValueClass("abc", 123);

        commLog.configureStringerForClass(ValueClass.class, ReflectingPropertyStringer.class);

        String s = commLog.getStringer(objects).toString(objects);

        assertEquals("['abc', {value1='abc', value2=123, optionalNestedValue=null}]", s);
    }

    @Test
    public void testArrayStringer_withPrimitiveArrays() {
        testArrayStringer_withPrimitiveArray(new char[]{'1', '2'}, "[1, 2]");
        testArrayStringer_withPrimitiveArray(new byte[]{1, 2}, "[1, 2]");
        testArrayStringer_withPrimitiveArray(new short[]{1, 2}, "[1, 2]");
        testArrayStringer_withPrimitiveArray(new int[]{1, 2}, "[1, 2]");
        testArrayStringer_withPrimitiveArray(new long[]{1, 2}, "[1, 2]");
        testArrayStringer_withPrimitiveArray(new float[]{1, 2}, "[1.0, 2.0]");
        testArrayStringer_withPrimitiveArray(new double[]{1, 2}, "[1.0, 2.0]");
        testArrayStringer_withPrimitiveArray(new boolean[]{true, false}, "[true, false]");
    }

    private void testArrayStringer_withPrimitiveArray(Object array, String expectedResult) {
        String s = commLog.getStringer(array).toString(array);
        assertEquals(expectedResult, s);
    }

    @Test
    public void testStringify_primitives() {
        int intPrimitive = 123;
        String s = commLog.getStringer(intPrimitive).toString(intPrimitive);
        assertEquals("123", s);

        boolean boolPrimitive = true;
        s = commLog.getStringer(boolPrimitive).toString(boolPrimitive);
        assertEquals("true", s);

        double doublePrimitive = 1.23;
        s = commLog.getStringer(doublePrimitive).toString(doublePrimitive);
        assertEquals("1.23", s);
    }

    @Test
    public void testStringifyURI_getsCorrectStringRepresentation() {
        URI uri = URI.create("http://host/path?queryParam=paramValue&queryParam2=paramValue2");

        assertEquals("Invalid precondition, failed to parse URI string?", "http", uri.getScheme());
        assertEquals("Invalid precondition, failed to parse URI string?", "host", uri.getHost());
        assertEquals("Invalid precondition, failed to parse URI string?", "/path", uri.getPath());
        assertEquals("Invalid precondition, failed to parse URI string?", "queryParam=paramValue&queryParam2=paramValue2", uri.getQuery());

        String s = commLog.getStringer(uri).toString(uri);
        assertEquals("http://host/path?queryParam=paramValue&queryParam2=paramValue2", s);
    }

    @Test
    public void testFoulStringer_exceptionDoesntPropagate() {
        commLog.configureStringerForClass(TestClass.class, FoulStringer.class);

        commLog.request("test", new TestClass());
        commLog.response(new TestClass());
    }

    private static class TestClass {
        private Object data;

        public void setData(Object data) {
            this.data = data;
        }
    }

    private static class TestClassWithEnum {
        private Enum theEnum;

        public void setTheEnum(Enum theEnum) {
            this.theEnum = theEnum;
        }
    }

    public static class FoulStringer extends Stringer {

        @Override
        String doStringify(final Object obj) {
            throw new RuntimeException("Failure!!!");
        }

        @Override
        public void addSecret(final String propertyName) {

        }
    }

}
