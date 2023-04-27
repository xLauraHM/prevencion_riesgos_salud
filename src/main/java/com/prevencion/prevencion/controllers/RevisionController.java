package com.prevencion.prevencion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.prevencion.prevencion.model.Revision;
import com.prevencion.prevencion.services.RevisionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/revisiones")
public class RevisionController {

    @Autowired
    RevisionService revisionService;

    @Value("${pagination.size}")
    int sizePage;

    @GetMapping(value = "/list")
    public ModelAndView list(Model model) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:list/1/id/asc");

        return modelAndView;
    }

    @GetMapping(value = "/list/{numPage}/{fieldSort}/{directionSort}")
    public ModelAndView listPage(Model model,
            @PathVariable("numPage") Integer numPage,
            @PathVariable("fieldSort") String fieldSort,
            @PathVariable("directionSort") String directionSort) {

        Pageable pageable = PageRequest.of(numPage - 1, sizePage,
                directionSort.equals("asc") ? Sort.by(fieldSort).ascending() : Sort.by(fieldSort).descending());

        Page<Revision> page = revisionService.findAll(pageable);

        List<Revision> revisiones = page.getContent();

        ModelAndView modelAndView = new ModelAndView("revisiones/list");
        modelAndView.addObject("revisiones", revisiones);

        modelAndView.addObject("numPage", numPage);
        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.addObject("totalElements", page.getTotalElements());

        modelAndView.addObject("fieldSort", fieldSort);
        modelAndView.addObject("directionSort", directionSort.equals("asc") ? "asc" : "desc");

        return modelAndView;
    }

    @GetMapping(path = { "/edit/{id}" })
    public ModelAndView edit(
            @PathVariable(name = "id", required = true) int id) {

        Revision revision = revisionService.findById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("revision", revision);
        modelAndView.setViewName("revisiones/edit");
        return modelAndView;
    }

    @GetMapping(path = { "/save" })
    public ModelAndView save(HttpSession session) {

        Revision revision = (Revision) session.getAttribute("revision");

        revisionService.save(revision);

        session.removeAttribute("revision");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:list");

        return modelAndView;
    }

    @GetMapping(path = { "/delete/{id}" })
    public ModelAndView delete(@PathVariable(name = "id", required = true) int id) {

        revisionService.delete(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/revisiones/list");
        return modelAndView;
    }
}
