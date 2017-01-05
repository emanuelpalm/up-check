package se.ltu.dcc.upcheck;

import se.ltu.dcc.upcheck.controller.ControllerLocateUppaal;
import se.ltu.dcc.upcheck.controller.Navigator;
import se.ltu.dcc.upcheck.model.uppaal.UppaalFolder;
import se.ltu.dcc.upcheck.model.uppaal.UppaalFolderException;
import se.ltu.dcc.upcheck.model.user.UserData;
import se.ltu.dcc.upcheck.util.EventSubscription;
import se.ltu.dcc.upcheck.util.JavaProcess;
import se.ltu.dcc.upcheck.view.Renderer;
import se.ltu.dcc.upcheck.view.Renderers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Application bootstrapping.
 * <p>
 * Resolves a classpath containing external dependencies and then starts the regular {@link Main} function in a new OS
 * process.
 * <p>
 * <i>Note to developers.</i> In case of debugging, the use of this class to start the application is not recommended,
 * this as it starts the actual application in another process that becomes unavailable to the Java debugger. Rather,
 * start the application with this class once, so that necessary configuration files can be generated, and then close
 * the application and start it again with the {@link Main} class. It might be necessary to complement the Java
 * classpath if starting directly via {@link Main}. Check the STDOUT printout from running this class in order to know
 * what to add to the classpath. The command used to start the new Java process should be available there.
 *
 * @see Main
 */
public final class Bootstrap {
    /**
     * Application bootstrapping function.
     *
     * @param args Provided application command line arguments.
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("UpCheck - Bootstrapping ...");

        int status;
        do {
            final String uppaalPath = UserData.uppaalFolderRoot();

            UppaalFolder uppaalFolder;
            try {
                uppaalFolder = UppaalFolder.create(uppaalPath);

            } catch (final UppaalFolderException e) {
                uppaalFolder = showLocateUppaal();
            }
            System.out.println("Using UPPAAL installation at: " + uppaalFolder);
            status = JavaProcess.exec(Main.class, Arrays.stream(uppaalFolder.jars())
                    .map(Path::toString)
                    .toArray(String[]::new));

        } while (status == Main.EXIT_STATUS_REBOOT);

        System.exit(status);
    }

    private static UppaalFolder showLocateUppaal() throws IOException, InterruptedException, ReflectiveOperationException {
        try (final Renderer<?> renderer = Renderers.CreateWindowRenderer();
             final EventSubscription ignored = renderer.onClose().subscribe(nil -> System.exit(0))) {

            final AtomicReference<UppaalFolder> atomicUppaalFolder = new AtomicReference<>();

            final Semaphore semaphore = new Semaphore(0);
            new Navigator(renderer).navigateTo(new ControllerLocateUppaal(uppaalFolder -> {
                atomicUppaalFolder.set(uppaalFolder);
                semaphore.release();
            }));
            semaphore.acquire();

            return atomicUppaalFolder.get();
        }
    }
}
