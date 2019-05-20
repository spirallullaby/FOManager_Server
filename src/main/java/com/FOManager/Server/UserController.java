package com.FOManager.Server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.FOManager.Server.Connection.UserActions;
import com.FOManager.Server.Models.ApiResultModel;
import com.FOManager.Server.Models.LoginModel;
import com.FOManager.Server.Models.SignUpModel;
import com.FOManager.Server.Models.UserModel;

@RestController
public class UserController {
	// email pattern
	Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

	@PostMapping("api/user/login")
	public ResponseEntity<ApiResultModel<UserModel>> Login(@RequestBody LoginModel model) {
		ApiResultModel<UserModel> result = new ApiResultModel<UserModel>();
		HttpStatus responseStatus = HttpStatus.OK;

		Matcher mat = pattern.matcher(model.EmailAddress);
		if (mat.matches()) {
			model.Password = hashPassword(model.Password, model.EmailAddress);

			UserActions userActions = new UserActions();
			UserModel resultModel = userActions.GetUser(model);

			if (resultModel != null) {
				result.Success = true;
				result.Result = resultModel;
			} else {
				result.ErrorMessage = "Incorrect email address or password.";
				responseStatus = HttpStatus.BAD_REQUEST;
			}
		} else {
			result.ErrorMessage = "Invalid email address!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<ApiResultModel<UserModel>>(result, responseStatus);
	}

	@PostMapping("api/user/signup")
	public ResponseEntity<ApiResultModel<UserModel>> SignUp(@RequestBody SignUpModel model) {
		ApiResultModel<UserModel> result = new ApiResultModel<UserModel>();
		HttpStatus responseStatus = HttpStatus.OK;

		Matcher mat = pattern.matcher(model.EmailAddress);
		if (mat.matches()) {
			UserActions userActions = new UserActions();
			Boolean userExists = userActions.UserWithEmailExists(model.EmailAddress);

			if (userExists) {
				result.ErrorMessage = "There is already user with this email address.";
				responseStatus = HttpStatus.BAD_REQUEST;
			} else if (model.Password.length() < 5) {
				result.ErrorMessage = "Password must contain at least 6 symbols!";
				responseStatus = HttpStatus.BAD_REQUEST;
			} else if (!model.Password.equals(model.RepeatPassword)) {
				result.ErrorMessage = "The passwords do not match.";
				responseStatus = HttpStatus.BAD_REQUEST;
			} else {
				model.Password = hashPassword(model.Password, model.EmailAddress);
				UserModel resultModel = userActions.InsertUser(model);
				if (resultModel != null) {
					result.Success = true;
					result.Result = resultModel;
				} else {
					result.ErrorMessage = "There was problem signing you up. Please try again!";
					responseStatus = HttpStatus.BAD_REQUEST;
				}
			}
		} else {
			result.ErrorMessage = "Invalid email address!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<ApiResultModel<UserModel>>(result, responseStatus);
	}

	@GetMapping("api/user/all")
	public List<UserModel> GetUsers() {
		UserActions userActions = new UserActions();
		return userActions.GetUsers();
	}

	private static final int ITERATIONS = 65536;
	private static final int KEY_LENGTH = 512;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

	private String hashPassword(String password, String salt) {
		char[] chars = password.toCharArray();
		byte[] bytes = salt.getBytes();

		PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

		Arrays.fill(chars, Character.MIN_VALUE);

		try {
			SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
			byte[] securePassword = fac.generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(securePassword);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			System.err.println("Exception encountered in hashPassword()");
			return null;

		} finally {
			spec.clearPassword();
		}
	}
}
