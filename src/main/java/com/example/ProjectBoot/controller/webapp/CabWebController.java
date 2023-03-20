package com.example.ProjectBoot.controller.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.ProjectBoot.dto.Page;
import com.example.ProjectBoot.service.CabService;

@Controller
@RequestMapping("/webapp/cabs")
public class CabWebController {

	@Autowired
	private CabService cabService;

	private Page setPage(String title) {
		Page page = new Page();
		page.setTitle(title);
		page.setTemplate("th_cab");
		return page;
	}
	
	@GetMapping()
	public ModelAndView Cabs(Model model) {
		model.addAttribute("cabs", cabService.findAll());
		model.addAttribute("page",setPage("Cabs"));
		return new ModelAndView("th_layout");
	}

}
