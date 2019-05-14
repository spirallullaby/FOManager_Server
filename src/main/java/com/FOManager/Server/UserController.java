package com.FOManager.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.FOManager.Server.Connection.UserActions;
import com.FOManager.Server.Models.ApiResultModel;
import com.FOManager.Server.Models.LoginModel;
import com.FOManager.Server.Models.SignUpModel;
import com.FOManager.Server.Models.UserModel;

@RestController
public class UserController {
	@PostMapping("api/user/login")
	ResponseEntity<ApiResultModel<UserModel>> Login(@RequestBody LoginModel model) {
		ApiResultModel<UserModel> result = new ApiResultModel<UserModel>();
		HttpStatus responseStatus = HttpStatus.OK;
		
		if (model.EmailAddress.equals("admin") && model.Password.equals("admin")) {
			result.Success = true;
			result.Result = new UserModel();
			result.Result.Id = 1;
			result.Result.EmailAddress = model.EmailAddress;
		} else {
			result.ErrorMessage = "Incorrect email address or password.";
			responseStatus = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<ApiResultModel<UserModel>>(result, responseStatus);
	}
	
	@PostMapping("api/user/signup")
	ResponseEntity<ApiResultModel<UserModel>> SignUp(@RequestBody SignUpModel model) {
		ApiResultModel<UserModel> result = new ApiResultModel<UserModel>();
		HttpStatus responseStatus = HttpStatus.OK;
		
		if (model.EmailAddress.equals("admin")) {
			result.ErrorMessage = "There is already user with this email address.";
			responseStatus = HttpStatus.BAD_REQUEST;
		} else if (!model.Password.equals(model.RepeatPassword)) {
			result.ErrorMessage = "The passwords do not match.";
			responseStatus = HttpStatus.BAD_REQUEST;
		} else {
			result.Success = true;
			result.Result = new UserModel();
			result.Result.Id = 1;
			result.Result.EmailAddress = model.EmailAddress;
		}
		
		return new ResponseEntity<ApiResultModel<UserModel>>(result, responseStatus);
	}
	
	@GetMapping("api/user/test")
	String Test(@RequestParam Integer user_id) {
		UserActions userActions = new UserActions();
		userActions.GetUserById(user_id);
		return "Test";
	}
}
