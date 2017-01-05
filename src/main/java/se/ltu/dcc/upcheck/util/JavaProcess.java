package se.ltu.dcc.upcheck.util;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Utilities for starting Java processes.
 */
public final class JavaProcess {
    private JavaProcess() {}

    /**
     * Runs main function in referenced class in separate system thread.
     *
     * @param clazz     main class to run
     * @param classpath classpath elements to add to current system classpath
     * @return exit value of executed process
     * @throws IOException          if failing to handle I/O of created process
     * @throws InterruptedException if interrupted while waiting for process to terminate
     */
    public static int exec(final Class clazz, final String... classpath) throws IOException, InterruptedException {
        final String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        final String classpath0 = System.getProperty("java.class.path") + (classpath.length > 0
                ? ":" + String.join(":", classpath)
                : "");
        return new ProcessBuilder(javaBin, "-cp", classpath0, clazz.getCanonicalName())
                .inheritIO()
                .start()
                .waitFor();
    }

}