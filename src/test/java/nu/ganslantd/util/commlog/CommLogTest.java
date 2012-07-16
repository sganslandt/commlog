package nu.ganslantd.util.commlog;

import nu.ganslandt.util.commlog.CommLogImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommLogTest {

    private CommLogImpl commLog;

    @Before
    public void setup() {
        commLog = (CommLogImpl) CommLogImpl.getLog("TestLog");
    }

    @Test
    public void testStringStringer() {
        String string = "abc22@ganslandt.nu";

        String s = commLog.getStringer(string).toString(string);

        assertEquals(string, s);
    }
}
