package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.FOManager.Server.Models.AddFOModel;

public class FinanceOperationActions {
	public Boolean InsertFO(AddFOModel model) {
		String insertStatement = "insert into {0} ({1},{2},{3}) values ('{4}','{5}','{6}')";
		insertStatement = insertStatement.replace("{0}", DBConstants.FOTable.name);
		insertStatement = insertStatement.replace("{1}", DBConstants.FOTable.user_id);
		insertStatement = insertStatement.replace("{2}", DBConstants.FOTable.sum);
		insertStatement = insertStatement.replace("{3}", DBConstants.FOTable.description);
		insertStatement = insertStatement.replace("{4}", Integer.toString(model.UserId));
		insertStatement = insertStatement.replace("{5}", Double.toString(model.Sum));
		insertStatement = insertStatement.replace("{6}", model.Description);
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(insertStatement);
			statement.execute();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
