package kw.tools.gallery.processing;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

            if (count >= images.size() || images.size() == 1)
            {
                return images;
            }

            // calculate indexes more sparse than originals, then round them to originals
            result.add(images.get(0));
            float maxIdx = images.size() - 1.0f;
            float step = maxIdx / (count - 1);
            for (float idx = step; Math.round(idx) <= maxIdx; idx += step)
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
