package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.persistence.TaskRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmptyTaskRepository extends TaskRepository<EmptyTask>
{
}
