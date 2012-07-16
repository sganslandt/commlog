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

        assertEquals("'" + 10 + "'", s);
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
        commLog.configureStringForClass(ValueClass.class, ReflectingPropertyStringer.class);
        ValueClass value = new ValueClass("abc", Integer.valueOf(123), new ValueClass("def", Integer.valueOf(456)));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2='123', optionalNestedValue={value1='def', value2='456', optionalNestedValue=null}}" ,s);
    }

    @Test
    public void testReflectingPropertyStringer_getsUsedWhenConfiguredViaPackage() {
        commLog.configureStringForPackage("nu.ganslandt.util.commlog", ReflectingPropertyStringer.class);
        ValueClass value = new ValueClass("abc", Integer.valueOf(123), new ValueClass("def", Integer.valueOf(456)));

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2='123', optionalNestedValue={value1='def', value2='456', optionalNestedValue=null}}" ,s);
    }
}
