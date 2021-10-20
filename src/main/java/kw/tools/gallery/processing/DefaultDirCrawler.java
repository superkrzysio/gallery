package kw.tools.gallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DefaultDirCrawler implements DirCrawler
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDirCrawler.class);
    @Override
    public void forEach(String path, Consumer<String> action) throws IOException
    {
        try(Stream<Path> filestream = Files.list(Paths.get(path)))
        {
            List<Path> files = filestream.collect(Collectors.toList());
            boolean executedAlready = false;
            for (Path file : files)
            {
                if(Files.isDirectory(file))
                {
                    forEach(file.toString(), action);
                }
                else if (!executedAlready)
                {
                    // if there are any files, execute the action only once on the dir
                    executedAlready = true;
                    action.accept(path);
                }
            }
        }
    }
}