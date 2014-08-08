package nu.ganslandt.util.commlog;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class SecretsTest {

    private CommLogImpl commLog;

    @Before
    public void setup() {
        commLog = (CommLogImpl) CommLogImpl.getLog("TestLog");
        commLog.addSecret("secretProperty");
    }

    @Test
    public void testMapStringer_stringMapsWithContent() {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
        linkedHashMap.put("result_code", "0");
        linkedHashMap.put("result_message", "Failed: Nothing is returned.");
        linkedHashMap.put("result_output", "json");
        linkedHashMap.put("secretProperty", "verySecretIndeed");

        String s = commLog.getStringer(linkedHashMap).toString(linkedHashMap);

        assertEquals("{'result_code': '0', 'result_message': 'Failed: Nothing is returned.', 'result_output': 'json', 'secretProperty': ********}", s);
    }

    @Test
    public void testReflectingPropertyStringer_masksSecretProperties() {
        commLog.configureStringerForPackage("nu.ganslandt.util.commlog", ReflectingPropertyStringer.class);
        ValueClassWithSecret value = new ValueClassWithSecret("abc", Integer.valueOf(123), new ValueClassWithSecret("def", Integer.valueOf(456), ""), "verySecretIndeed");

        String s = commLog.getStringer(value).toString(value);

        assertEquals("{value1='abc', value2=123, optionalNestedValue={value1='def', value2=456, optionalNestedValue=null, secretProperty=********}, secretProperty=********}", s);
    }

}
