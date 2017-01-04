package se.ltu.d7031e.emapal4.upcheck;

import se.ltu.d7031e.emapal4.upcheck.controller.ControllerLocateUppaal;
import se.ltu.d7031e.emapal4.upcheck.controller.Navigator;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolder;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalFolderException;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.util.EventSubscription;
import se.ltu.d7031e.emapal4.upcheck.util.JavaProcess;
import se.ltu.d7031e.emapal4.upcheck.view.Renderer;
import se.ltu.d7031e.emapal4.upcheck.view.Renderers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Application bootstrapping.
 * <p>
 * Resolving a classpath containing external dependencies and then starts the regular {@link Main} function in a new
 * process.
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
