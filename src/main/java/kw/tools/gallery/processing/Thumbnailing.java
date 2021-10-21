package kw.tools.gallery.processing;

public interface Thumbnailing
{
    void generate(String source, String target);

    ProcessingStatus getStatus();
}
