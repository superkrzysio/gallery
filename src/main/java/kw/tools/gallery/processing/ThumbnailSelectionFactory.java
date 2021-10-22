package kw.tools.gallery.processing;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns a method of selecting items (thumbnail candidates) from the list (gallery).<br/>
 * If there are less items in the source list than requested <tt>count</tt>, then all are returned.<br/>
 * If there are 0 source items or 0 <tt>count</tt> requested, an empty list is returned.
 * The implementations do not guarantee to create new instance of a list but they do not modify the source list.
 * <ul>
 *     <li>FIRST - only first items are selected</li>
 *     <li>LAST - only last items are selected</li>
 *     <li>SPREAD - returns first item, last item and others between, spread evenly </li>
 * </ul>
 * FIRST
 */
@Component
public class ThumbnailSelectionFactory
{
    public ThumbnailSelector get(ThumbnailSelector.Strategy strategy, int count)
    {
        switch (strategy)
        {
            case FIRST:
                return new SelectFirstStrategy(count);
            case SPREAD:
                return new SelectSpreadStrategy(count);
            case LAST:
                return new SelectLastStrategy(count);
        }
        throw new IllegalStateException("Selection strategy not selected");
    }

    private static class SelectFirstStrategy implements ThumbnailSelector
    {
        private final int count;

        private SelectFirstStrategy(int count)
        {
            this.count = count;
        }

        @Override
        public List<String> select(List<String> images)
        {
            return images.subList(0, Math.min(images.size(), count));
        }
    }

    private static class SelectSpreadStrategy implements ThumbnailSelector
    {
        private final int count;

        private SelectSpreadStrategy(int count)
        {
            this.count = count;
        }

        @Override
        public List<String> select(List<String> images)
        {
            List<String> result = new ArrayList<>();
            if (count == 0)
            {
                return result;
            }

            if (count >= images.size())
            {
                return images;
            }

            /* calculate indexes more sparse than originals, then round them to originals
            for example:

                            0   1   2   3   4   5   6   7   8   9   A
            Source list:    |   |   |   |   |   |   |   |   |   |   |  - source size:  11
            Calculated idx: |    |    |    |    |    |    |    |    |  - requested count: 9

                            0   1       3   4   5   6       8   9   A
            Returned idx:   |   |       |   |   |   |       |   |   |  - rounded to integers

             */
            result.add(images.get(0));
            float maxIdx = images.size() - 1.0f;
            float step = maxIdx / (count - 1);
            for (float idx = step; Math.round(idx) <= Math.round(maxIdx); idx += step)
            {
                result.add(images.get(Math.round(idx)));
            }
            return result;
        }
    }

    private static class SelectLastStrategy implements ThumbnailSelector
    {
        private final int count;

        private SelectLastStrategy(int count)
        {
            this.count = count;
        }

        @Override
        public List<String> select(List<String> images)
        {
            return images.subList(Math.max(images.size() - count, 0), images.size());
        }
    }
}
