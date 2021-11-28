package kw.tools.gallery.processing;

import java.util.List;

public interface FilenameEncoder
{
    String encodeThumbFilename(String targetDir, String originalPath);

    List<String> decodeThumbFilename(String source);
}
