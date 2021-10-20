package kw.tools.gallery.processing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailingFactory
{
    @Value("${cache.dir}")
    private String cacheDir;
    
    public ThumbnailingBuilder forId(String id)
    {
        return new ThumbnailingBuilder().withId(id).withDir(cacheDir);
    }

    public static class ThumbnailingBuilder
    {
        private String id;
        private String dir;

        private ThumbnailingBuilder withId(String id)
        {
            this.id = id;
            return this;
        }

        private ThumbnailingBuilder withDir(String dir)
        {
            this.dir = dir;
            return this;
        }

        public Thumbnailing singleImageThumbs()
        {
            return new SingleImageThumbnailing().withOutputDir(dir).withOutputId(id);
        }

        public Thumbnailing multiThumbs()
        {
            return new MultiImageThumbnailing().withOutputDir(dir).withOutputId(id);
        }

    }
}
