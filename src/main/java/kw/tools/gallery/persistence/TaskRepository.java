package kw.tools.gallery.persistence;

import kw.tools.gallery.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface TaskRepository<T extends Task> extends JpaRepository<Task, String>
{
    void deleteAll();

    List<T> findByStatus(Task.Status status);
}
