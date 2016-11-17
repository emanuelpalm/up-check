package se.ltu.d7031e.emapal4.upcheck;

import se.ltu.d7031e.emapal4.upcheck.view.ViewLocateUppaal;
import se.ltu.d7031e.emapal4.upcheck.view.Window;

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

        final Window window = new Window(new ViewLocateUppaal());
        window.onClose().subscribe(nil -> {
            System.out.println("Bye!");
            System.exit(0);
        });
    }
}
