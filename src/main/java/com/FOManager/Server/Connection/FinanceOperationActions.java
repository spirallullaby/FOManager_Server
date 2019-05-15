package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.FOManager.Server.Models.AddFOModel;
import com.FOManager.Server.Models.ExtractOperationsModel;
import com.FOManager.Server.Models.FOModel;

import com.opencsv.CSVWriter;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class FinanceOperationActions {
	private static final String CSV_FILE_PATH = "./history.csv";

	public Boolean InsertFO(AddFOModel model) {
		String date = String.format("%s-%s-%s", model.Date.getYear(), model.Date.getMonth(), model.Date.getDate());
		String insertStatement = String.format("insert into %s (%s,%s,%s,%s) values ('%s','%s','%s','%s')",
				DBConstants.FOTable.name, DBConstants.FOTable.user_id, DBConstants.FOTable.sum,
				DBConstants.FOTable.description, DBConstants.FOTable.date, Integer.toString(model.UserId),
				Double.toString(model.Sum), model.Description, date);
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

	public List<FOModel> GetFinanceOperations(ExtractOperationsModel model) {
		ArrayList<FOModel> result = new ArrayList<FOModel>();
		try {
			String startDate = String.format("%s-%s-%s", model.StartDate.get(Calendar.YEAR),
					model.StartDate.get(Calendar.MONTH) + 1, model.StartDate.get(Calendar.DATE));
			String endDate = String.format("%s-%s-%s", model.EndDate.get(Calendar.YEAR),
					model.EndDate.get(Calendar.MONTH) + 1, model.EndDate.get(Calendar.DATE));
			String selectStatement = String.format("SELECT * FROM %s WHERE %s=%s AND '%s'<=%s AND %s<='%s'",
					DBConstants.FOTable.name, DBConstants.FOTable.user_id, model.UserId, startDate,
					DBConstants.FOTable.date, DBConstants.FOTable.date, endDate);
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				FOModel returnModel = new FOModel();
				returnModel.Id = resultSet.getInt(DBConstants.FOTable.id);
				returnModel.Sum = resultSet.getInt(DBConstants.FOTable.sum);

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("Europe/Sofia"));
				cal.setTime(sdf.parse(resultSet.getString(DBConstants.FOTable.date)));

				java.util.Date dateParsed = sdf.parse(resultSet.getString(DBConstants.FOTable.date));

				returnModel.Date = dateParsed;
				returnModel.Description = resultSet.getString(DBConstants.FOTable.description);
				returnModel.UserId = resultSet.getInt(DBConstants.FOTable.user_id);
				result.add(returnModel);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Boolean emailHistory(ExtractOperationsModel model) throws IOException {
		Boolean result = true;
		List<FOModel> history = GetFinanceOperations(model);
		Writer writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH));

		CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] { "Operation date", "Description", "Sum" });
		double total = 0;
		for (FOModel foModel : history) {
			csvWriter.writeNext(
					new String[] { foModel.Date.toString(), foModel.Description, Double.toString(foModel.Sum) });
			total += foModel.Sum;
		}
		csvWriter.writeNext(new String[] { "", "Total:", Double.toString(total) });
		csvWriter.close();
		return result;
	}
}
