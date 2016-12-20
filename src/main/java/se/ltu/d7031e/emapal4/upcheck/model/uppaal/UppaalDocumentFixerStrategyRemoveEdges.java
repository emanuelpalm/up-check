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
            // TODO: Make implementation less naive.
            int x = 0;
            for (final Node template : Nodes.iterableOf(templates)) {
                int y = 0;
                for (final Node node : Nodes.iterableOf(template.getFirst())) {
                    if (node instanceof Edge) {
                        final Document clone = (Document) document.clone();
                        Nodes.getChildByIndexes(clone, x, y)
                                .orElseThrow(IllegalStateException::new)
                                .remove();

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
