package kw.tools.gallery.controllers;

import kw.tools.gallery.models.Repository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RepositoryController
{
    public static final List<Repository> allRepos = new ArrayList<>();  // todo: temporary

    static
    {
        allRepos.add(new Repository("Repo1"));
        allRepos.add(new Repository("My repo 2"));
        allRepos.add(new Repository("Test"));
    }

    @RequestMapping("/repositories")
    public ModelAndView listRepositories()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("repositories");
        modelAndView.addObject("repositories", allRepos);
        return modelAndView;
    }

    @RequestMapping("/repositories/view/{id}")
    public String viewRepository(@PathVariable("id") String id)
    {
        return "Viewing id: " + id;
    }

    @RequestMapping("/repositories/add")
    public RedirectView addRepository(@ModelAttribute Repository repository)
    {
        // adding...
        allRepos.add(repository);
        return new RedirectView("/repositories");
    }
}
