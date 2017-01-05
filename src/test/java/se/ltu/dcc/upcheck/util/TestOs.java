package se.ltu.dcc.upcheck.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOs {
    @Test
    public void shouldResolveOperatingSystemNamesCorrectly() {
        assertEquals(Os.Family.LINUX, Os.Family.resolve("Linux"));
        assertEquals(Os.Family.MAC_OS_X, Os.Family.resolve("Mac OS X"));
        assertEquals(Os.Family.WINDOWS, Os.Family.resolve("Windows 10"));
        assertEquals(Os.Family.OTHER, Os.Family.resolve("FreeBSD"));
    }
}
