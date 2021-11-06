package kw.tools.gallery.controllers;

import kw.tools.gallery.processing.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TaskController
{
    @Autowired
    private Tasks tasks;

    @RequestMapping("/tasks")
    public ModelAndView printTasks()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("tasks");
        modelAndView.addObject("tasks", tasks.getAll());
        modelAndView.addObject("tasksFinished", tasks.getFinished().size());
        modelAndView.addObject("tasksTotal", tasks.getAll().size());
        return modelAndView;
    }
}
