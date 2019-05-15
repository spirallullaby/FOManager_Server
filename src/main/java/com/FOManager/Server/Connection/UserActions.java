package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.FOManager.Server.Models.LoginModel;
import com.FOManager.Server.Models.SignUpModel;
import com.FOManager.Server.Models.UserModel;

public class UserActions {
	public UserModel GetUser(LoginModel model) {
		UserModel resultModel = null;
		String selectStatement = String.format("select %s, %s from %s where %s = '%s' and \"%s\" = '%s';",
				DBConstants.UserTable.id,
				DBConstants.UserTable.email_address, 
				DBConstants.UserTable.name, 
				DBConstants.UserTable.email_address,
				model.EmailAddress,
				DBConstants.UserTable.password,
				model.Password);
		
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet  resultSet = statement.executeQuery();
			if (resultSet.next()) {
				resultModel = new UserModel();
				resultModel.Id = resultSet.getInt(DBConstants.UserTable.id);
				resultModel.EmailAddress = resultSet.getString(DBConstants.UserTable.email_address);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultModel;
	}
	
	public Boolean UserWithEmailExists(String email_address) {
		String selectStatement = String.format("select %s from %s where %s = '%s';",
				DBConstants.UserTable.id,
				DBConstants.UserTable.name, 
				DBConstants.UserTable.email_address,
				email_address);
		
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet  resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public UserModel InsertUser(SignUpModel model) {
		UserModel resultModel = null;
		String insertStatement = String.format("insert into %s (%s,\"%s\") values ('%s','%s');",
				DBConstants.UserTable.name,
				DBConstants.UserTable.email_address,
				DBConstants.UserTable.password,
				model.EmailAddress,
				model.Password);
		String selectStatement = String.format("select %s, %s from %s where %s = '%s';",
				DBConstants.UserTable.id,
				DBConstants.UserTable.email_address,
				DBConstants.UserTable.name,
				DBConstants.UserTable.email_address,
				model.EmailAddress);
		
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(insertStatement);
			statement.execute();
			
			statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				resultModel = new UserModel();
				resultModel.Id = resultSet.getInt(DBConstants.UserTable.id);
				resultModel.EmailAddress = resultSet.getString(DBConstants.UserTable.email_address);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultModel;
	}
}
