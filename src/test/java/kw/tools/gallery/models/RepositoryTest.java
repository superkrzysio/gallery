package kw.tools.gallery.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoryTest
{

    @Test
    public void getSafeNameAlphanumeric()
    {
        String path = "abcABC1230_abcABC1230_";
        Repository repo = new Repository("abcABC1230_abcABC1230_");
        Assertions.assertEquals(path, Repository.createSafeName(path));
    }

    @Test
    public void getSafeNameFullPath()
    {
        String path = "/a/b/c.x";
        Repository repo = new Repository(path);
        Assertions.assertEquals("-a-b-c-x", Repository.createSafeName(path));
    }

}