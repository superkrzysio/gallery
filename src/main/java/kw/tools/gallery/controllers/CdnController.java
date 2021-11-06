package kw.tools.gallery.controllers;

import kw.tools.gallery.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

/**
 * Workaround for browser security, when you can't render content from local filesystem on a webpage.<br/>
 * Simply serving the images from cache folder. <br/>
 * Security: none
 */
@RestController
public class CdnController
{
    private static final Logger LOG = LoggerFactory.getLogger(CdnController.class);

    @Autowired
    private CacheUtils cacheUtils;

    @RequestMapping(value = "/cdn/{repo}/{gallery}/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    byte[] getImage(@PathVariable("repo") String repo, @PathVariable("gallery") String gallery, @PathVariable("image") String image)
    {
        try
        {
            return Files.readAllBytes(cacheUtils.getCacheDirPathForGallery(repo, gallery).resolve(image));
        } catch (IOException e)
        {
            LOG.error("Invalid image requested: {}/{}/{}", repo, gallery, image);
            return getErrorImage();
        }
    }

    private byte[] getErrorImage()
    {
        try
        {
            return getClass().getResourceAsStream("/404.png").readAllBytes();
        } catch (IOException e)
        {
            LOG.error("Missing resource");
            throw new UncheckedIOException(e);
        }
    }
}
