package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.taskengine.core.TaskRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErroneousTaskRepository extends TaskRepository<ErroneousTask>
{
}
