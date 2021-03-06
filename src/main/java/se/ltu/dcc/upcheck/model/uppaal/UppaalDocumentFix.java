package se.ltu.dcc.upcheck.model.uppaal;

import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Node;
import se.ltu.dcc.upcheck.model.uppaal.util.Nodes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents some set of fixes made to a faulty {@link Document} that makes it correct.
 */
public abstract class UppaalDocumentFix {
    private UppaalDocumentFix() {}

    /**
     * Represents a set of fixes all to be applied.
     */
    public static class And extends UppaalDocumentFix {
        private final UppaalDocumentFix[] fixes;

        private And(final UppaalDocumentFix[] fixes) {
            this.fixes = fixes;
        }

        /**
         * Creates new fix AND group from given fixes.
         *
         * @param fixes fixes to group
         * @return created fix object
         */
        static UppaalDocumentFix Of(final UppaalDocumentFix... fixes) {
            return (fixes.length == 1)
                    ? fixes[0]
                    : new And(fixes);
        }

        @Override
        public String toString() {
            return Arrays.stream(fixes)
                    .map(Object::toString)
                    .collect(Collectors.joining(" and ", "(", ")"));
        }
    }

    /**
     * A node removal fix.
     */
    public static class Remove extends UppaalDocumentFix {
        private final Node node;

        private Remove(final Node node) {
            this.node = node;
        }

        /**
         * Creates new node removal fix of given list of nodes.
         *
         * @param nodes nodes to remove
         * @return created fix object
         */
        static UppaalDocumentFix Of(final List<Node> nodes) {
            return And.Of(nodes.stream()
                    .map(Remove::Of)
                    .collect(Collectors.toList())
                    .toArray(new UppaalDocumentFix[nodes.size()]));
        }

        /**
         * Creates new node removal fix.
         *
         * @param node node to remove
         * @return created fix object
         */
        static UppaalDocumentFix Of(final Node node) {
            return new Remove(node);
        }

        @Override
        public String toString() {
            return "remove " + Nodes.toString(node);
        }
    }
}

