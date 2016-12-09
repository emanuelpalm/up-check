package se.ltu.d7031e.emapal4.upcheck.model.uppaal;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.Node;
import se.ltu.d7031e.emapal4.upcheck.model.uppaal.util.Nodes;

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
            int x = 0, y = 0;
            // TODO: Make implementation less naive.
            for (final Node template : Nodes.iterableOf(templates)) {
                System.out.println(Nodes.toString(template, false));
                for (final Node node : Nodes.iterableOf(template.getFirst())) {
                    System.out.println(Nodes.toString(node, false));
                    if (node instanceof Edge) {
                        final Document clone = (Document) document.clone(); // TODO: Not behaving as expected.
                        Nodes.getChildByIndexes(clone, x, y)
                                //.orElseThrow(IllegalStateException::new)
                                //.remove();
                                .ifPresent(Node::remove);

                        if (predicate.test(clone)) {
                            fixes.add(UppaalDocumentFix.Remove.Of(node));
                        }
                    }
                    y++;
                }
                x++;
            }
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e);

        } catch (final UppaalDocumentFixerTimeoutException e) {
            e.printStackTrace();
        }
        return fixes;
    }
}
