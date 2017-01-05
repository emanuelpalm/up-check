package se.ltu.d7031e.emapal4.upcheck;

import se.ltu.d7031e.emapal4.upcheck.controller.ControllerVerifySystem;
import se.ltu.d7031e.emapal4.upcheck.controller.Navigator;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolder;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolderException;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxyException;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.Renderer;
import se.ltu.d7031e.emapal4.upcheck.view.Renderers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Application main class.
 * <p>
 * The application requires files from a local UPPAAL installation to be available on the classpath. This is achieved
 * via the {@link Bootstrap} class, containing its own main method used to help a user to locate the mentioned UPPAAL
 * folder.
 */
public final class Main {
    /**
     * Signals to the application {@link Bootstrap} that the application should be bootstrapped again.
     */
    static final int EXIT_STATUS_REBOOT = 2;

    /**
     * Application main function.
     *
     * @param args provided application command line arguments
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("UpCheck - Running ...");

        final Renderer<?> renderer = Renderers.CreateWindowRenderer();
        renderer.onClose().subscribe(nil -> Executors.newSingleThreadScheduledExecutor()
                .schedule(
                        () -> System.exit(0),
                        500,
                        TimeUnit.MILLISECONDS)
        );
        final Navigator navigator = new Navigator(renderer);
        try {
            final String uppaalPath = UserData.uppaalFolderRoot();
            final UppaalFolder uppaalFolder = UppaalFolder.create(uppaalPath);
            final UppaalProxy uppaalProxy = new UppaalProxy(uppaalFolder);
            final ControllerVerifySystem controllerVerifySystem = new ControllerVerifySystem(uppaalProxy);
            navigator.navigateTo(controllerVerifySystem);

        } catch (final UppaalProxyException | UppaalFolderException e) {
            renderer.showException(null, e);
            UserData.setUppaalFolderRoot(null);
            reboot();

        } catch (final Throwable e) {
            renderer.showException(null, e);
            System.exit(1);
        }
    }

    /**
     * Exists application and signals to {@link Bootstrap} process that a reboot is requested.
     */
    public static void reboot() {
        System.exit(EXIT_STATUS_REBOOT);
    }
}
