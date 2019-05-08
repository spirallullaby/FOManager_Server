package com.FOManager.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.FOManager.Server.Models.AddFOModel;
import com.FOManager.Server.Models.ApiResultModel;
import com.FOManager.Server.Models.FOModel;


@RestController
public class FOManagerController {
	@PostMapping("api/FOManager/add")	
	ResponseEntity<ApiResultModel<FOModel>> Add(@RequestBody AddFOModel model) {
        ApiResultModel<FOModel> result = new ApiResultModel<FOModel>();
		HttpStatus responseStatus = HttpStatus.OK;
        
        if (model.UserId != 1)
        {
            result.ErrorMessage = "The user does not exist.";
			responseStatus = HttpStatus.BAD_REQUEST;
        } else {
	        result.Success = true;
	        result.Result = new FOModel();
	        result.Result.Id = 1;
			result.Result.UserId = model.UserId;
			result.Result.Sum = model.Sum;
			result.Result.Description = model.Description;
        }
		
		return new ResponseEntity<ApiResultModel<FOModel>>(result, responseStatus);
	}
	
	@GetMapping("api/FOManager/exportHistory")	
	ResponseEntity<String> ExportHistory(@RequestParam(value = "dateFrom", required = false) String dateFrom, @RequestParam(value = "dateTo", required = false) String dateTo) {
		HttpStatus responseStatus = HttpStatus.OK;
		
		return new ResponseEntity<String>(dateFrom, responseStatus);
	}
}
