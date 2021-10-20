package kw.tools.gallery.processing;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Component
public class SingleImageThumbnailing extends AbstractThumbnailing
{
    private ProcessingStatus status = new ProcessingStatus();

    @Override
    public void generate(String path)
    {
        try
        {
            // todo: temp
            String files = Files.list(Path.of(path))
                    .filter(item -> Files.isRegularFile(item))
                    .map(item -> item.getFileName().toString())
                    .collect(Collectors.joining(", "));
            System.out.printf("Files in '%s': %s%n", path, files);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
