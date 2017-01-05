package se.ltu.dcc.upcheck.model.uppaal.util;

import com.uppaal.model.core2.*;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Various UPPAAL {@link Node} utilities.
 */
public class Nodes {
    private Nodes() {}

    /**
     * Gets {@link Node} sibling by offset.
     *
     * @param node   node of which sibling is to be acquired
     * @param offset offset, in nodes, from {@code node}
     * @return sibling node, if any, at specified offset
     */
    public static Optional<Node> getSiblingByOffset(Node node, int offset) {
        while (node != null && offset-- > 0) {
            node = node.getNext();
        }
        return Optional.ofNullable(node);
    }

    /**
     * Gets {@link Node} child by index.
     *
     * @param parent node of which child is to be acquired
     * @param index  index of child
     * @return child node, if any, at specified index
     */
    public static Optional<Node> getChildByIndex(final Node parent, final int index) {
        final Node child = Optional.ofNullable(parent)
                .map(Node::getFirst)
                .orElse(null);

        return getSiblingByOffset(child, index);
    }

    /**
     * Gets {@link Node} nested child by indexes.
     *
     * @param parent  node of which nested child is to be acquired
     * @param indexes indexes of child nodes, each such representing its own hierarchy level
     * @return child node, if any, at location specified by given indexes
     */
    public static Optional<Node> getChildByIndexes(final Node parent, final int... indexes) {
        Node child = parent;
        for (final int index : indexes) {
            child = getChildByIndex(child, index).orElse(null);
            if (child == null) {
                break;
            }
        }
        return Optional.ofNullable(child);
    }

    /**
     * Creates iterable of given node, useful for iterating through all node sibling on the same hierarchy level after
     * given start node.
     *
     * @param start node in linked list of nodes to start iterating from
     * @return iterable
     */
    public static Iterable<Node> iterableOf(final Node start) {
        return () -> new Iterator<Node>() {
            private Node node = start;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public Node next() {
                try {
                    return node;

                } finally {
                    node = node.getNext();
                }
            }
        };
    }

    /**
     * Produces string representation of provided {@link Node}.
     *
     * @param node node of which to get string representation
     * @return node string representation
     */
    public static String toString(final Node node) {
        return toString(node, true, false);
    }

    /**
     * Produces string representation of provided {@link Node}.
     *
     * @param node node of which to get string representation
     * @return node string representation
     */
    public static String toString(final Node node, final boolean includeParent, final boolean includeChildren) {
        final String name = (String) Optional.ofNullable(node.getProperty("name"))
                .map(Property::getValue)
                .orElse(null);

        final String type, extra;
        if (node instanceof AbstractLocation) {
            type = "Location";
            extra = "";
        } else if (node instanceof AbstractTemplate) {
            type = "Template";
            extra = "";
        } else if (node instanceof Edge) {
            final Edge edge = (Edge) node;
            type = "Edge";
            extra = " source: " + toString(edge.getSource(), false, false) + " target: " + toString(edge.getTarget(), false, false);

        } else {
            type = node.getClass().getSimpleName();
            extra = "";
        }

        final String parent = includeParent ? Optional.ofNullable(node.getParent())
                .filter(parent0 -> parent0 instanceof Node)
                .map(parent0 -> toString((Node) parent0, false, false))
                .orElse(null) : null;

        final String children = includeChildren ? StreamSupport.stream(iterableOf(node.getFirst()).spliterator(), false)
                .map(child -> toString(child, false, true))
                .collect(Collectors.joining(" ", "[", "]")) : null;

        return type + "{" +
                (name != null && name.length() > 0 ? " name: \"" + name + "\"" : "") +
                (parent != null && parent.length() > 0 ? " parent: " + parent : "") +
                (children != null && children.length() > 0 ? " children: " + children : "") +
                extra +
                " }";
    }
}
