package com.gdis.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestExample {

	@RequestMapping(value = "/api/example", method = RequestMethod.GET)
	public Map<String, Object> example() {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("id", 1);
		model.put("message", "Hello");
		return model;
	}
}
