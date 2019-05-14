package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.FOManager.Server.Models.LoginModel;
import com.FOManager.Server.Models.UserModel;

public class UserActions {
	public UserModel InsertUser(LoginModel model) {
		String insertStatement = "insert into {0} ({1},{2}) values ('{3}','{4}')";
		insertStatement = insertStatement.replace("{0}", DBConstants.UserTable.name);
		insertStatement = insertStatement.replace("{1}", DBConstants.UserTable.email_address);
		insertStatement = insertStatement.replace("{2}", DBConstants.UserTable.password);
		insertStatement = insertStatement.replace("{3}", model.EmailAddress);
		insertStatement = insertStatement.replace("{4}", model.Password);
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(insertStatement);
			statement.executeQuery();
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void GetUserById(int user_id) {
		String selectStatement = "select {0}, {1} from {2} where {3} = {4}";
		selectStatement = selectStatement.replace("{0}", DBConstants.UserTable.id);
		selectStatement = selectStatement.replace("{1}", DBConstants.UserTable.email_address);
		selectStatement = selectStatement.replace("{2}", DBConstants.UserTable.name);
		selectStatement = selectStatement.replace("{3}", DBConstants.UserTable.id);
		selectStatement = selectStatement.replace("{4}", Integer.toString(user_id));
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet  rs = statement.executeQuery();
			while (rs.next())
			{
			    System.out.print("Column 1 returned ");
			    System.out.println(rs.getString(1));
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
