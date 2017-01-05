package se.ltu.dcc.upcheck.model.uppaal;

import com.uppaal.model.core2.Document;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A report containing various alternatives at fixing some faulty UPPAAL {@link Document}.
 */
public class UppaalDocumentFixerReport implements Iterable<UppaalDocumentFix> {
    private final ArrayList<UppaalDocumentFix> fixes = new ArrayList<>();

    /**
     * @param fixes UPPAAL document fixes to add to report
     */
    void add(final Iterable<UppaalDocumentFix> fixes) {
        fixes.forEach(this.fixes::add);
    }

    /**
     * @return whether or not report is empty
     */
    public boolean isEmpty() {
        return fixes.isEmpty();
    }

    @Override
    public Iterator<UppaalDocumentFix> iterator() {
        return fixes.iterator();
    }

    @Override
    public String toString() {
        return fixes.toString();
    }
}
