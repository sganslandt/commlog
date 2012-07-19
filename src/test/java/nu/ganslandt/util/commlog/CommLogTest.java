package nu.ganslandt.util.commlog;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class CommLogTest {

    private CommLogImpl commLog;

    @Before
    public void setup() {
        commLog = (CommLogImpl) CommLogImpl.getLog("TestLog");
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

        assertEquals("{value1='abc', value2=123, optionalNestedValue={value1='def', value2=456, optionalNestedValue=null}}" ,s);
    }

    @Test
    public void testReflectingPropertyStringer_getsUsedWhenConfiguredViaPackage() {
        commLog.configureStringerForPackage("nu.ganslandt.util.commlog", ReflectingPropertyStringer.class);
        ValueClass value = new ValueClass("abc", Integer.valueOf(123), new ValueClass("def", Integer.valueOf(456)));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2=123, optionalNestedValue={value1='def', value2=456, optionalNestedValue=null}}" ,s);
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
    public void testToStringString_primitives() {
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
}
