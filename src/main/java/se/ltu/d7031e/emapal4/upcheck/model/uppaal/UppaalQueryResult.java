package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.QueryVerificationResult;
import com.uppaal.model.system.symbolic.SymbolicTransition;

import java.util.ArrayList;

/**
 * Holds the result of some UPPAAL query.
 */
public class UppaalQueryResult implements QueryFeedback {
    @Override
    public void appendText(final String s) {
        System.out.println("appendText(" + s + ")");
    }

    @Override
    public void setCurrent(final int pos) {
        System.out.println("setCurrent(" + pos + ")");
    }

    @Override
    public void setTrace(final char result, final String feedback, final ArrayList<SymbolicTransition> trace, final int cycle, final QueryVerificationResult verificationResult) {
        System.out.println("setTrace(" + result + ", " + feedback + ", " + trace + ", " + cycle + ", " + verificationResult + ")");
    }

    @Override
    public void setFeedback(final String feedback) {
        System.out.println("setFeedback(" + feedback + ")");
    }

    @Override
    public void setLength(final int length) {
        System.out.println("setLength(" + length + ")");
    }

    @Override
    public void setProgress(final int load, final long vm, final long rss, final long cached, final long avail, final long swap, final long swapfree, final long user, final long sys, final long timestamp) {
        System.out.println("setProgress(" + load + ", " + vm + ", " + rss + ", " + cached + ", " + avail + ", " + swap + ", " + swapfree + ", " + user + ", " + sys + ", " + timestamp + ")");
    }

    @Override
    public void setProgressAvail(final boolean availability) {
        System.out.println("setProgressAvail(" + availability + ")");
    }

    @Override
    public void setResultText(final String s) {
        System.out.println("setResultText(" + s + ")");
    }

    @Override
    public void setSystemInfo(final long vmsize, final long physsize, final long swapsize) {
        System.out.println("setSystemInfo(" + vmsize + ", " + physsize + ", " + swapsize + ")");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
