package se.ltu.d7031e.emapal4.upcheck;

import se.ltu.d7031e.emapal4.upcheck.controller.ControllerLocateUppaal;
import se.ltu.d7031e.emapal4.upcheck.controller.ControllerVerifySystem;
import se.ltu.d7031e.emapal4.upcheck.controller.Navigator;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalPathStatus;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.UppaalProxy;
import se.ltu.d7031e.emapal4.upcheck.model.user.UserData;
import se.ltu.d7031e.emapal4.upcheck.view.Renderer;
import se.ltu.d7031e.emapal4.upcheck.view.Renderers;

import java.nio.file.Paths;

/**
 * Application main class.
 *
 * Only contains main function.
 */
public class Main {
    /**
     * Application main function.
     *
     * @param args Provided application command line arguments.
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("UpCheck");

        final Renderer<?> renderer = Renderers.CreateWindowRenderer();
        renderer.onClose().subscribe(nil -> {
            System.out.println("Bye!");
            System.exit(0);
        });

        final Navigator navigator = new Navigator(renderer);

        final String pathToUppaalInstallation = UserData.uppaalPath();
        if (UppaalPathStatus.validate(pathToUppaalInstallation) == UppaalPathStatus.OK) {
            final UppaalProxy uppaalProxy = new UppaalProxy(Paths.get(pathToUppaalInstallation));
            final ControllerVerifySystem controllerVerifySystem = new ControllerVerifySystem(uppaalProxy);
            navigator.navigateTo(controllerVerifySystem);
        } else {
            navigator.navigateTo(new ControllerLocateUppaal());
        }
    }
}
