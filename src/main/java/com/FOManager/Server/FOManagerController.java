package com.FOManager.Server;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.FOManager.Server.Connection.FinanceOperationActions;
import com.FOManager.Server.Models.AddFOModel;
import com.FOManager.Server.Models.ApiResultModel;
import com.FOManager.Server.Models.ExtractOperationsModel;
import com.FOManager.Server.Models.FOModel;

@RestController
public class FOManagerController {
	@PostMapping("api/FOManager/add")
	ResponseEntity<ApiResultModel<Void>> AddFO(@RequestBody AddFOModel model) {
		ApiResultModel<Void> result = new ApiResultModel<Void>();
		HttpStatus responseStatus = HttpStatus.OK;

		if (model != null) {
			FinanceOperationActions foActions = new FinanceOperationActions();
			Boolean insertRes = foActions.InsertFO(model);
			if (insertRes) {
				result.Success = true;
			} else {
				result.ErrorMessage = "There was problem inserting the model in the database!";
				responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			result.ErrorMessage = "The model is empty!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<ApiResultModel<Void>>(result, responseStatus);
	}

	@GetMapping("api/FOManager/exportHistory")
	ResponseEntity<ApiResultModel<List<FOModel>>> ExportHistory(@RequestBody ExtractOperationsModel model) {
		ApiResultModel<List<FOModel>> result = new ApiResultModel<List<FOModel>>();
		HttpStatus responseStatus = HttpStatus.OK;
		
		if (model != null) {
			FinanceOperationActions foActions = new FinanceOperationActions();
			List<FOModel> selectResult = foActions.GetFinanceOperations(model);
			if (selectResult != null) {
				result.Success = true;
				result.Result = selectResult;
			} else {
				result.ErrorMessage = "There was problem inserting the model in the database!";
				responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			result.ErrorMessage = "The model is empty!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<ApiResultModel<List<FOModel>>>(result, responseStatus);
	}
	
	@PostMapping("api/FOManager/emailHistory")
	ResponseEntity<ApiResultModel<String>> EmailHistory(@RequestBody ExtractOperationsModel model) {
		ApiResultModel<String> result = new ApiResultModel<String>();
		HttpStatus responseStatus = HttpStatus.OK;
		
		if (model != null) {
			FinanceOperationActions foActions = new FinanceOperationActions();
			try {
			Boolean selectResult = foActions.emailHistory(model);
			result.Success = selectResult;
			}
			catch(Exception ex) {
				result.ErrorMessage = "Something went wrong!";
				result.Result = ex.getMessage();
				result.Success = false;
				responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			result.ErrorMessage = "The model is empty!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<ApiResultModel<String>>(result, responseStatus);
	}
}
