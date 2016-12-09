package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.core2.Document;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a strategy employable in modifying some faulty UPPAAL {@link Document} in order to make it correct.
 */
public interface UppaalDocumentFixerStrategy {
    /**
     * Applies strategy to given UPPAAL {@link Document}, using provided predicate to test for successful applications.
     *
     * @param system    system to modify
     * @param predicate test determining if some modification was valid
     * @return list of successful modifications
     */
    List<UppaalDocumentFix> apply(final Document system, final Predicate<Document> predicate);
}
