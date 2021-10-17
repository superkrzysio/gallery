package kw.tools.gallery.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest
{

    @Test
    public void getSafeNameAlphanumeric()
    {
        String path = "abcABC1230_abcABC1230_";
        Repository repo = new Repository("abcABC1230_abcABC1230_");
        Assertions.assertEquals(path, repo.getSafeName());
    }

    @Test
    public void getSafeNameFullPath()
    {
        Repository repo = new Repository("/a/b/c.x");
        Assertions.assertEquals("-a-b-c-x", repo.getSafeName());
    }

}