package com.example.ProjectBoot.controller.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ProjectBoot.bean.HomeBean;
import com.example.ProjectBoot.dto.Page;

@Controller
@RequestMapping("/")
public class HomeWebController {

	private Page setPage(String title) {
		Page page = new Page();
		page.setTitle(title);
		page.setTemplate("th_home");
		return page;
	}

	@GetMapping("/")
	public String Home(Model model) {
		model.addAttribute("homeBean", new HomeBean());
		model.addAttribute("page",setPage("Home"));
		return "th_layout";
	}
}

