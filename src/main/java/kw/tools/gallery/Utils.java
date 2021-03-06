package kw.tools.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.StringTokenizer;

/**
 * Global utils. <br/>
 * Use this class statically, do not autowire.
 */
@Component          // hack to populate static fields from properties
public class Utils
{
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static String fileViewerCommand;

    public Utils(@Value("${system.file.viewer.command}") String fileViewerCommandTemp)
    {
        Utils.fileViewerCommand = fileViewerCommandTemp;
    }

    /**
     * Call a system command to open a file browser for the given path. Due to dumb {@link StringTokenizer} used by exec(),
     * paths with spaces will not work. <br />
     * Obviously, it should never be exposed to the internet.
     */
    public static void openInFileBrowser(String path)
    {
        try
        {
            String[] cmd = new String[] { fileViewerCommand, path };
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
