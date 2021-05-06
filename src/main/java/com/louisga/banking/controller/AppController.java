package com.louisga.banking.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppController {	

	@GetMapping({"/","/dashboard"})
	String getDashboard() {
		return "pages/dashboard";
	}

}
