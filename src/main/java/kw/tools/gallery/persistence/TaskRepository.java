package kw.tools.gallery.persistence;

import kw.tools.gallery.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository<T extends Task> extends JpaRepository<Task, Integer>
{
    void deleteAll();

    Optional<Task> findFirstByStatusOrderByIdAsc(Task.Status status);
}
