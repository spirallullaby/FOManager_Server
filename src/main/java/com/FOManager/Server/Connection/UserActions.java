package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;

import com.FOManager.Server.Models.LoginModel;
import com.FOManager.Server.Models.SignUpModel;
import com.FOManager.Server.Models.UserModel;

import ch.qos.logback.core.joran.spi.ActionException;

public class UserActions {
	public UserModel GetUser(LoginModel model) {
		UserModel resultModel = null;
		String selectStatement = String.format("select %s, %s from %s where %s = '%s' and \"%s\" = '%s';",
				DBConstants.UserTable.id, DBConstants.UserTable.email_address, DBConstants.UserTable.name,
				DBConstants.UserTable.email_address, model.EmailAddress, DBConstants.UserTable.password,
				model.Password);

		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
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
		String selectStatement = String.format("select %s from %s where %s = '%s';", DBConstants.UserTable.id,
				DBConstants.UserTable.name, DBConstants.UserTable.email_address, email_address);

		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String getUserEmail(int userId) throws AddressException {
		String selectStatement = String.format("select %s from %s where %s = '%s';",
				DBConstants.UserTable.email_address, DBConstants.UserTable.name, DBConstants.UserTable.id, userId);

		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString(DBConstants.UserTable.email_address);
			} else {
				throw new AddressException("Could not find user with that ID!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public UserModel InsertUser(SignUpModel model) {
		UserModel resultModel = null;
		String insertStatement = String.format("insert into %s (%s,\"%s\") values ('%s','%s');",
				DBConstants.UserTable.name, DBConstants.UserTable.email_address, DBConstants.UserTable.password,
				model.EmailAddress, model.Password);
		String selectStatement = String.format("select %s, %s from %s where %s = '%s';", DBConstants.UserTable.id,
				DBConstants.UserTable.email_address, DBConstants.UserTable.name, DBConstants.UserTable.email_address,
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
	
	public List<UserModel> GetUsers() {
		ArrayList<UserModel> users = new ArrayList<UserModel>();
		
		String selectStatement = String.format("select %s, %s from %s;", DBConstants.UserTable.id,
				DBConstants.UserTable.email_address, DBConstants.UserTable.name);
		
		try {
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				UserModel user = new UserModel();
				user.Id = resultSet.getInt(DBConstants.UserTable.id);
				user.EmailAddress = resultSet.getString(DBConstants.UserTable.email_address);
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
}
