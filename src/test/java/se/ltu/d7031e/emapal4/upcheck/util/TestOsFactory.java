package se.ltu.d7031e.emapal4.upcheck.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOsFactory {
    @Test
    public void shouldResolveOperatingSystemNamesCorrectly() {
        assertEquals(OsFactory.Family.LINUX, OsFactory.Family.resolveFromOsName("Linux"));
        assertEquals(OsFactory.Family.MAC_OS_X, OsFactory.Family.resolveFromOsName("Mac OS X"));
        assertEquals(OsFactory.Family.WINDOWS, OsFactory.Family.resolveFromOsName("Windows 10"));
        assertEquals(OsFactory.Family.OTHER, OsFactory.Family.resolveFromOsName("FreeBSD"));
    }
}
