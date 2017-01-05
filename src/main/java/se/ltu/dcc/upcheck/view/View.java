package se.ltu.dcc.upcheck.view;

import java.util.Optional;

/**
 * An object that can act as an MVC view.
 */
public interface View {
    /**
     * @return context menu to display, if any
     */
    default Optional<Menu> menu() {
        return Optional.empty();
    }

    /**
     * @param message Error message to be displayed to application user.
     * @param e       Exception to be displayed to application user.
     */
    void showException(final String message, final Throwable e);

    /**
     * Context menu option.
     */
    abstract class Menu {
        private final String name;

        private Menu(final String name) {
            this.name = name;
        }

        /**
         * @return menu option name
         */
        public final String name() {
            return name;
        }

        /**
         * Visits all menu nodes.
         *
         * @param context context to provide during menu item visits
         * @param visitor visitor to use
         * @return provided context
         */
        public abstract <R> R visit(final R context, final Visitor<R> visitor);

        /**
         * A menu option with an associated action.
         */
        public static final class Option extends Menu {
            private final Runnable action;

            /**
             * Creates new leaf menu option.
             *
             * @param name   option name
             * @param action action to execute when menu option is selected
             */
            public Option(final String name, final Runnable action) {
                super(name);
                this.action = action;
            }

            /**
             * @return menu option action
             */
            public Runnable action() {
                return action;
            }

            @Override
            public <R> R visit(final R context, final Visitor<R> visitor) {
                visitor.onOption(context, this);
                return context;
            }
        }

        /**
         * A menu option with child menu options.
         */
        public static final class Category extends Menu {
            private final Menu[] children;

            /**
             * Creates new branch menu option.
             *
             * @param name     option name
             * @param children child menu options
             */
            public Category(final String name, final Menu... children) {
                super(name);
                this.children = children;
            }

            /**
             * @return children of branch menu option
             */
            public Menu[] children() {
                return children;
            }

            @Override
            public <R> R visit(R context, Visitor<R> visitor) {
                final R childContext = visitor.onCategory(context, this);
                for (final Menu child : children) {
                    child.visit(childContext, visitor);
                }
                return context;
            }
        }

        /**
         * Visitor useful for visiting all nodes of some {@link Menu} tree.
         */
        public interface Visitor<R> {
            /**
             * Called when visiting {@link Category} node.
             *
             * @param context  visitor context
             * @param category visited category node
             * @return context provided to children of category
             */
            R onCategory(final R context, final Category category);

            /**
             * @param context visitor context
             * @param option  visited option node
             */
            void onOption(final R context, final Option option);
        }
    }
}
