package kw.tools.gallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Class used to encode a filename with some configuration options, e.g. selection strategy.
 */
@Component
public class FilenameEncoder
{
    private static final Logger LOG = LoggerFactory.getLogger(FilenameEncoder.class);

    @Value("${thumbnails.selection.strategy}")
    private ThumbnailSelector.Strategy selectionStrategy;

    public String encodeThumbFilename(String targetDir, String originalPath)
    {
        if (!targetDir.endsWith("/"))
            targetDir += "/";
        return targetDir +
                filenameEncodingConstantPart() +
                Path.of(originalPath).getFileName().toString();
    }

    public List<String> decodeThumbFilename(String source)
    {
        try
        {
            return Files.list(Path.of(source))
                    .filter(Files::isRegularFile)
                    .filter(file -> getFilename(file).startsWith(filenameEncodingConstantPart()))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e)
        {
            LOG.error("Wrong source cache folder given to retrieve thumbnails: " + source, e);
            return Collections.EMPTY_LIST;
        }
    }

    private String filenameEncodingConstantPart()
    {
        return this.getClass().getName() +
                "_" +
                selectionStrategy.name().toLowerCase(Locale.ROOT) +
                "_";
    }

    private String getFilename(Path p)
    {
        return p.getFileName().toString();
    }
}
