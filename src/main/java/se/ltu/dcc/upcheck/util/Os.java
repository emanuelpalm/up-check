package se.ltu.dcc.upcheck.util;

/**
 * Operating system utilities.
 */
public class Os {
    /**
     * Runtime operating system.
     */
    static final Family CURRENT = Family.resolve(System.getProperty("os.name"));

    private Os() {}

    /**
     * Denotes operating system family.
     */
    public enum Family {
        LINUX,
        MAC_OS_X,
        WINDOWS,
        OTHER;

        /**
         * Resolves OS from provided name.
         *
         * @param osName OS name
         * @return resolved OS family
         */
        public static Family resolve(final String osName) {
            final String osNameLowerCase = osName.toLowerCase();
            if (osNameLowerCase.contains("linux")) {
                return LINUX;
            } else if (osNameLowerCase.contains("mac")) {
                return MAC_OS_X;
            } else if (osNameLowerCase.contains("windows")) {
                return WINDOWS;
            } else {
                return OTHER;
            }
        }
    }

    /**
     * Allows an object to be created differently depending on the runtime operating system family.
     */
    public interface Factory<T> {
        /**
         * @return New instance, created by platform-specific code.
         */
        default T create() {
            switch (CURRENT) {
                case LINUX:
                    return createOnLinux();
                case MAC_OS_X:
                    return createOnMacOsX();
                case WINDOWS:
                    return createOnWindows();
                case OTHER:
                    return createOnOther();
            }
            throw new IllegalStateException("Unknown OS family: " + CURRENT);
        }

        /**
         * @return New Linux object instance.
         */
        default T createOnLinux() {
            return createOnOther();
        }

        /**
         * @return New Mac OS X object instance.
         */
        default T createOnMacOsX() {
            return createOnOther();
        }

        /**
         * @return New Windows object instance.
         */
        default T createOnWindows() {
            return createOnOther();
        }

        /**
         * @return New general object instance.
         */
        default T createOnOther() {
            throw new IllegalStateException();
        }
    }
}
