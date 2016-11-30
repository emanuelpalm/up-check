package se.ltu.d7031e.emapal4.upcheck.util;

/**
 * Allows an object to be created differently depending on the runtime operating system family.
 */
public abstract class OsFactory<T> {
    private static final Family CURRENT_OS_FAMILY = Family.resolveFromOsName(System.getProperty("os.name"));

    /**
     * @return New instance, created by platform-specific code.
     */
    public final T create() {
        switch (CURRENT_OS_FAMILY) {
            case LINUX:
                return createOnLinux();
            case MAC_OS_X:
                return createOnMacOsX();
            case WINDOWS:
                return createOnWindows();
            case OTHER:
                return createOnOther();
        }
        throw new IllegalStateException("Unknown OS family: " + CURRENT_OS_FAMILY);
    }

    /**
     * @return New Linux object instance.
     */
    protected T createOnLinux() { return createOnOther(); }

    /**
     * @return New Mac OS X object instance.
     */
    protected T createOnMacOsX() { return createOnOther(); }

    /**
     * @return New Windows object instance.
     */
    protected T createOnWindows() { return createOnOther(); }

    /**
     * @return New general object instance.
     */
    protected T createOnOther() { throw new IllegalStateException(); }

    enum Family {
        LINUX,
        MAC_OS_X,
        WINDOWS,
        OTHER;

        static Family resolveFromOsName(final String osName) {
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
}
