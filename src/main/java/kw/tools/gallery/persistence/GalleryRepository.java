package kw.tools.gallery.persistence;

import kw.tools.gallery.models.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, String>
{
    List<Gallery> findByRepositoryId(String id);

    int countByRepositoryId(String id);

    @Transactional
    @Modifying
    void deleteByRepositoryId(String id);

    Optional<Gallery> findByRepositoryIdAndPath(String repoId, String path);
}
