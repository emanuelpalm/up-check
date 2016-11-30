package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import se.ltu.d7031e.emapal4.upcheck.util.DynamicInterface;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Holds the result of some UPPAAL query.
 */
public class UppaalQueryResult {
    /**
     * Creates new UPPAAL query result object from given dynamic com.uppaal.engine.QueryFeedback interface instance.
     *
     * @param queryFeedback dynamic com.uppaal.engine.QueryFeedback interface
     */
    UppaalQueryResult(final DynamicInterface queryFeedback) {
        queryFeedback.registerProxyMethod("appendText", new Class<?>[]{String.class}, args -> {
            appendText((String) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setCurrent", new Class<?>[]{int.class}, args -> {
            setCurrent((int) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setFeedback", new Class<?>[]{String.class}, args -> {
            setFeedback((String) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setLength", new Class<?>[]{int.class}, args -> {
            setLength((int) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setProgress", new Class<?>[]{int.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class, long.class}, args -> {
            setProgress((int) args[0], (long) args[1], (long) args[2], (long) args[3], (long) args[4], (long) args[5], (long) args[6], (long) args[7], (long) args[8], (long) args[9]);
            return null;
        });
        queryFeedback.registerProxyMethod("setProgressAvail", new Class<?>[]{boolean.class}, args -> {
            setProgressAvail((boolean) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setResultText", new Class<?>[]{String.class}, args -> {
            setResultText((String) args[0]);
            return null;
        });
        queryFeedback.registerProxyMethod("setSystemInfo", new Class<?>[]{long.class, long.class, long.class}, args -> {
            setSystemInfo((long) args[0], (long) args[1], (long) args[2]);
            return null;
        });
        queryFeedback.registerProxyMethod("setTrace", new Class<?>[]{char.class, Vector.class, int.class}, args -> {
            setTrace((char) args[0], (Vector) args[1], (int) args[2]);
            return null;
        });
        queryFeedback.registerProxyMethod("setTrace", new Class<?>[]{char.class, String.class, ArrayList.class, int.class}, args -> {
            setTrace((char) args[0], (String) args[1], (ArrayList) args[2], (int) args[3]);
            return null;
        });
        queryFeedback.registerProxyMethod("toString", new Class<?>[0], nil -> toString());
    }

    private void appendText(final String s) {
        System.out.println("appendText(" + s + ")");
    }

    private void setCurrent(final int pos) {
        System.out.println("setCurrent(" + pos + ")");
    }

    private void setFeedback(final String feedback) {
        System.out.println("setFeedback(" + feedback + ")");
    }

    private void setLength(final int length) {
        System.out.println("setLength(" + length + ")");
    }

    private void setProgress(final int load, final long vm, final long rss, final long cached, final long avail, final long swap, final long swapfree, final long user, final long sys, final long timestamp) {
        System.out.println("setProgress(" + load + ", " + vm + ", " + rss + ", " + cached + ", " + avail + ", " + swap + ", " + swapfree + ", " + user + ", " + sys + ", " + timestamp + ")");
    }

    private void setProgressAvail(final boolean availability) {
        System.out.println("setProgressAvail(" + availability + ")");
    }

    private void setResultText(final String s) {
        System.out.println("setResultText(" + s + ")");
    }

    private void setSystemInfo(final long vmsize, final long physsize, final long swapsize) {
        System.out.println("setSystemInfo(" + vmsize + ", " + physsize + ", " + swapsize + ")");
    }

    private void setTrace(final char result, final Vector trace, final int cycle) {
        // TODO: Gather trace.
        System.out.println("setTrace(" + result + ", " + trace + ", " + cycle + ")");
    }

    private void setTrace(final char result, final String feedback, final ArrayList trace, final int cycle) {
        // TODO: Gather trace.
        System.out.println("setTrace(" + result + ", " + feedback + ", " + trace + ", " + cycle + ")");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
