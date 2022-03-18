package kw.tools.gallery.taskengine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository<T extends Task> extends JpaRepository<Task, Integer>
{
    void deleteAll();

    Optional<Task> findFirstByStatusOrderByIdAsc(Task.Status status);
}
