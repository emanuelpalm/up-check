package se.ltu.d7031e.emapal4.upcheck;

import se.ltu.d7031e.emapal4.upcheck.controller.ControllerLocateUppaal;
import se.ltu.d7031e.emapal4.upcheck.controller.Navigator;
import se.ltu.d7031e.emapal4.upcheck.view.Renderer;
import se.ltu.d7031e.emapal4.upcheck.view.Renderers;

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
        navigator.navigateTo(new ControllerLocateUppaal());
    }
}
