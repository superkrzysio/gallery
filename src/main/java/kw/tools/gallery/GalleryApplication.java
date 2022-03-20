package kw.tools.gallery;

import kw.tools.gallery.processing.ImgUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GalleryApplication
{
    public static void main(String[] args)
    {
        ImgUtils.init();
        SpringApplication.run(GalleryApplication.class, args);
    }

}
