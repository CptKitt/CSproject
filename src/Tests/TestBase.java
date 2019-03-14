package Tests;

/** Basic testing class. */
public class TestBase {
    public void log(String message, String e, String a) {
        System.out.println(message + " expected " + e + " but was " + a);
    }
    
    public void assertTrue(String message, boolean condition) {
        if (!condition)
            log(message, ""+true, ""+false);
    }
    
    public void assertFalse(String message, boolean condition) {
        if (condition)
            log(message, ""+false, ""+true);
    }
    
    public void assertEquals(String message, Object expected, Object actual) {
        if (!expected.equals(actual))
            log(message, ""+expected, ""+actual);
    }
    
    public void assertNotEquals(String message, Object expected, Object actual) {
        if (expected.equals(actual))
            log(message, ""+expected, ""+actual);
    }
    
    public void assertEquals(String message, int expected, int actual) {
        if (expected != actual)
            log(message, ""+expected, ""+actual);
    }
    
    public void assertEquals(String message, double expected, double actual) {
        assertEquals(message, expected, actual, 0.00001);
    }
    
    public void assertEquals(String message, double expected, double actual, double epsilon) {
        if (Math.abs(expected - actual) > epsilon)
            log(message, ""+expected, ""+actual);
    }
    
    public void assertNotNull(String message, Object object) {
        if (object == null)
            log(message, "not null", "null");
    }
    
    public void assertNull(String message, Object object) {
        if (object != null)
            log(message, "null", "not null");
    }
}
