package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Node;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.util.Nodes;
import se.ltu.d7031e.emapal4.upcheck.util.Combinatorics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * UPPAAL document fixer strategy that systematically removes all combinations of template edges.
 */
public class UppaalDocumentFixerStrategyRemoveEdges implements UppaalDocumentFixerStrategy {
    @Override
    public List<UppaalDocumentFix> apply(final Document document, final Predicate<Document> predicate) {
        final ArrayList<UppaalDocumentFix> fixes = new ArrayList<>();
        try {
            final AbstractTemplate templates = document.getTemplates();
            if (templates == null) {
                return Collections.emptyList();
            }
            final ArrayList<NodePath> nodePaths = new ArrayList<>();
            {
                int offsetTemplate = 0;
                for (final Node template : Nodes.iterableOf(templates)) {
                    int offsetTemplateChild = 0;
                    for (final Node node : Nodes.iterableOf(template.getFirst())) {
                        if (node instanceof Edge) {
                            nodePaths.add(new NodePath(offsetTemplate, offsetTemplateChild));
                        }
                        offsetTemplateChild++;
                    }
                    offsetTemplate++;
                }
            }
            Combinatorics.combinations(nodePaths, nodePathCombination -> {
                try {
                    final Document clone = (Document) document.clone();

                    final ArrayList<Node> nodes = new ArrayList<>(nodePathCombination.size());
                    nodePathCombination.forEach(nodePath -> nodes.add(Nodes.getChildByIndexes(clone, nodePath.offsetTemplate, nodePath.offsetTemplateChild)
                            .orElseThrow(IllegalStateException::new)));
                    nodes.forEach(Node::remove);

                    if (predicate.test(clone)) {
                        fixes.add(UppaalDocumentFix.Remove.Of(nodes));
                    }
                } catch (final CloneNotSupportedException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (final UppaalDocumentFixerTimeoutException e) {
            e.printStackTrace();
        }
        return fixes;
    }

    /**
     * Represents the relative path to some node.
     */
    private static class NodePath {
        private final int offsetTemplate;
        private final int offsetTemplateChild;

        NodePath(final int offsetTemplate, final int offsetTemplateChild) {
            this.offsetTemplate = offsetTemplate;
            this.offsetTemplateChild = offsetTemplateChild;
        }
    }
}
