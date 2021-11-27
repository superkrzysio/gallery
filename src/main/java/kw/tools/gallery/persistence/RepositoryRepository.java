package kw.tools.gallery.persistence;

import kw.tools.gallery.models.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryRepository extends JpaRepository<Repository, String>
{
    
}
