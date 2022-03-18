package kw.tools.gallery.services;

import org.springframework.stereotype.Service;

/**
 * Cdn is a sarcastic name for endpoint that serves image data.
 */
@Service
public class CdnService
{
    /**
     * @param repo  Repository id
     * @param gal   Gallery id
     * @param thumb Thumbnail id
     * @return URL that can be inserted into &lt;img src=""/&gt;
     */
    public String getCdnUrl(String repo, String gal, String thumb)
    {
        return String.format("/cdn/%s/%s/%s", repo, gal, thumb);
    }
}
