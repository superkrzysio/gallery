package kw.tools.gallery.processing;

public interface Thumbnailing
{
    void generate(String path, String targetId);
    ProcessingStatus getStatus();
}
