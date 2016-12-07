package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.system.UppaalSystem;
import se.ltu.d7031e.emapal4.upcheck.util.EventBroker;
import se.ltu.d7031e.emapal4.upcheck.util.EventPublisher;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Holds a collection of UPPAAL queries that may be asked against some {@link UppaalSystem} using a {@link UppaalProxy}.
 * <p>
 * Thread safe.
 */
public class UppaalQueries {
    private static final Predicate<String> IS_COMMENT_LINE = Pattern.compile("\\s*//.*").asPredicate();
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final EventBroker<UppaalQuery> onQueryUpdated = new EventBroker<>();
    private final Object lock = new Object();

    private String original;
    private Set<UppaalQuery> queries;

    /**
     * Creates new empty collection of UPPAAL queries.
     */
    public UppaalQueries() {
        this.original = "";
        this.queries = Collections.emptySet();
    }

    private UppaalQueries(final String original) {
        this.original = original;
        this.queries = parseBytes(original.getBytes(CHARSET), CHARSET);
    }

    /**
     * Reads contents of file at given path into unvalidated UPPAAL queries.
     * <p>
     * Throws {@link UncheckedIOException} if reading the file at the given path fails.
     *
     * @param path path to file to read
     * @param cs   file character encoding
     * @return UPPAAL queries object, which contain no queries if the file at the provided path was empty
     */
    public static UppaalQueries readFile(final Path path, final Charset cs) {
        try {
            return new UppaalQueries(new String(Files.readAllBytes(path), cs));

        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Turns given string into unvalidated UPPAAL queries.
     *
     * @param string string to read
     * @return UPPAAL queries object, which contain no queries if the provided string contained only whitespace
     */
    public static UppaalQueries readString(final String string) {
        return new UppaalQueries(string);
    }

    private static Set<UppaalQuery> parseBytes(final byte[] bytes, final Charset cs) {
        final AtomicInteger lineCounter = new AtomicInteger(0);
        final AtomicBoolean inComment = new AtomicBoolean(false);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return new BufferedReader(new InputStreamReader(inputStream, cs))
                .lines()
                .map(line -> {
                    if (inComment.get()) {
                        final int indexEnd = line.lastIndexOf("*/");
                        if (indexEnd >= 0) {
                            line = line.substring(indexEnd + 2);
                            inComment.set(false);
                        }
                    } else {
                        final int indexBegin = line.indexOf("/*");
                        if (indexBegin >= 0) {
                            line = line.substring(0, indexBegin);
                            inComment.set(true);
                        }
                    }
                    if (IS_COMMENT_LINE.test(line)) {
                        line = "";
                    }
                    return new UppaalQuery(lineCounter.incrementAndGet(), line);
                })
                .filter(UppaalQuery::isNotEmpty)
                .collect(Collectors.toSet());
    }

    /**
     * Clears all contained queries.
     */
    public void clear() {
        synchronized (lock) {
            original = "";
            queries.clear();
        }
    }

    /**
     * Replaces the current set of queries with those found in the given string.
     * <p>
     * Any differences between the given set of queries and the current set of queries are published via {@link
     * #onQueryUpdated()}.
     *
     * @param string string of queries to read
     */
    public void update(final String string) {
        final Set<UppaalQuery> newQueries = parseBytes(string.getBytes(CHARSET), CHARSET);
        synchronized (lock) {
            for (final UppaalQuery query : newQueries) {
                if (!queries.contains(query)) {
                    onQueryUpdated.publish(query);
                }
            }
            original = string;
            queries = newQueries;
        }
    }

    /**
     * @return unmodifiable set of {@link UppaalQuery} objects
     */
    public Set<UppaalQuery> queries() {
        synchronized (lock) {
            return Collections.unmodifiableSet(queries);
        }
    }

    /**
     * Publishes query updates.
     * <p>
     * A query update occurs whenever the {@link #update(String)} method is invoked, and any new queries are found who
     * have no immediate counterparts in the existing set of queries.
     *
     * @return query update event publisher
     */
    public EventPublisher<UppaalQuery> onQueryUpdated() {
        return onQueryUpdated;
    }

    @Override
    public String toString() {
        return original;
    }
}
