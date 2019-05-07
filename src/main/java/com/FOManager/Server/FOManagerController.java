package com.FOManager.Server;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.ws.rs.core.*;

@RestController
public class FOManagerController {

	@PostMapping("/FOManager/add")	
	FOModel AddFO(@RequestBody FOModel model) {
		return model;
	}
	
	@GetMapping("/ping")
	String Ping() {
		return "[ {\"value\": \"pinged service\"}]";
	}
	
	@PostMapping("/login")
	ApiResultModel Login(@RequestBody LoginModel model) {
		ApiResultModel result = new ApiResultModel();
		result.success = true;
		return result;
	}
}
