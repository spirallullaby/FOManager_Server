package com.FOManager.Server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.FOManager.Server.Connection.FinanceOperationActions;
import com.FOManager.Server.Models.AddFOModel;
import com.FOManager.Server.Models.ApiResultModel;
import com.FOManager.Server.Models.ExtractOperationsModel;
import com.FOManager.Server.Models.FOModel;

@RestController
public class FOManagerController {
	@PostMapping("api/FOManager/add")
	ResponseEntity<ApiResultModel<FOModel>> AddFO(@RequestBody AddFOModel model) {
		ApiResultModel<FOModel> result = new ApiResultModel<FOModel>();
		HttpStatus responseStatus = HttpStatus.OK;

		if (model != null) {
			FinanceOperationActions foActions = new FinanceOperationActions();
			Boolean insertRes = foActions.InsertFO(model);
			if (insertRes) {
				result.Success = true;
				result.Result = new FOModel();
				result.Result.UserId = model.UserId;
				result.Result.Sum = model.Sum;
				result.Result.Description = model.Description;
			} else {
				result.ErrorMessage = "There was problem inserting the model in the database!";
				responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			result.ErrorMessage = "The model is empty!";
			responseStatus = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<ApiResultModel<FOModel>>(result, responseStatus);
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
}
