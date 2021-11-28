package kw.tools.gallery.controllers;

import kw.tools.gallery.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TaskController
{
    @Autowired
    private TaskService taskService;

    @RequestMapping("/tasks")
    public ModelAndView printTasks()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("tasks");
        // todo: fix for new task design and migrate to vaadin
//        modelAndView.addObject("tasks", taskService.getAll());
//        modelAndView.addObject("tasksFinished", taskService.getFinished().size());
//        modelAndView.addObject("tasksTotal", taskService.getAll().size());
        return modelAndView;
    }
}
