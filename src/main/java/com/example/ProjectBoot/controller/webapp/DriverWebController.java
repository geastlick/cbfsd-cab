package com.example.ProjectBoot.controller.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.ProjectBoot.dto.Page;
import com.example.ProjectBoot.service.DriverService;

@Controller
@RequestMapping("/webapp/drivers")
public class DriverWebController {

	@Autowired
	private DriverService driverService;

	private Page setPage(String title) {
		Page page = new Page();
		page.setTitle(title);
		page.setTemplate("th_driver");
		return page;
	}
	
	@GetMapping()
	public ModelAndView Cabs(Model model) {
		model.addAttribute("drivers", driverService.findAll());
		model.addAttribute("page",setPage("Drivers"));
		return new ModelAndView("th_layout");
	}
}
