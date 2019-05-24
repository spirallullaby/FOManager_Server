package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class FinanceOperationActions {
	private static final String CSV_FILE_PATH = "./history";
	private static final String CSV_EXTENSION = ".csv";

	public Boolean InsertFO(AddFOModel model) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = dateFormat.format(model.Date);
		//String date = String.format("%s-%s-%s %s:%s:%s", model.Date.getYear() + 1900, model.Date.getMonth() + 1, model.Date.getDate(), model.Date.getHours(), model.Date.getMinutes(), model.Date.getSeconds());
		String insertStatement = String.format("insert into %s (%s,%s,%s,%s,%s) values ('%s','%s','%s','%s','%s')",
				DBConstants.FOTable.name, DBConstants.FOTable.user_id, DBConstants.FOTable.type, DBConstants.FOTable.sum,
				DBConstants.FOTable.description, DBConstants.FOTable.date, Integer.toString(model.UserId), Integer.toString(model.Type),
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
					model.StartDate.get(Calendar.MONTH) + 1, model.StartDate.get(Calendar.DATE) + 1);
			String endDate = String.format("%s-%s-%s", model.EndDate.get(Calendar.YEAR),
					model.EndDate.get(Calendar.MONTH) + 1, model.EndDate.get(Calendar.DATE) + 1);
			String selectStatement = String.format("SELECT * FROM %s WHERE %s=%s AND '%s'<=%s AND %s<='%s'",
					DBConstants.FOTable.name, DBConstants.FOTable.user_id, model.UserId, startDate,
					DBConstants.FOTable.date, DBConstants.FOTable.date, endDate);
			Connection conn = PostgreConnector.getConnection();
			PreparedStatement statement = conn.prepareStatement(selectStatement);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				FOModel returnModel = new FOModel();
				returnModel.Id = resultSet.getInt(DBConstants.FOTable.id);
				returnModel.UserId = resultSet.getInt(DBConstants.FOTable.user_id);
				returnModel.Type = resultSet.getInt(DBConstants.FOTable.type);
				returnModel.Sum = resultSet.getDouble(DBConstants.FOTable.sum);

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("Europe/Sofia"));
				cal.setTime(sdf.parse(resultSet.getString(DBConstants.FOTable.date)));

				java.util.Date dateParsed = sdf.parse(resultSet.getString(DBConstants.FOTable.date));

				returnModel.Date = dateParsed;
				returnModel.Description = resultSet.getString(DBConstants.FOTable.description);
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

	public Boolean emailHistory(ExtractOperationsModel model) throws IOException, AddressException, MessagingException {
		Boolean result = true;
		String path = generateCSV(model);
		sendMail(path, model.UserId);
		return result;
	}

	private String generateCSV(ExtractOperationsModel model) throws IOException {
		List<FOModel> history = GetFinanceOperations(model);
		String path = CSV_FILE_PATH + model.UserId + CSV_EXTENSION;
		Writer writer = Files.newBufferedWriter(Paths.get(path));

		CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
		csvWriter.writeNext(new String[] { "Operation date", "Description", "Income", "Expense", "Balance" });
		double total = 0;
		for (FOModel foModel : history) {
			if (foModel.Type == 1) {
				total += foModel.Sum;
				csvWriter.writeNext(
					new String[] { foModel.Date.toString(), foModel.Description, Double.toString(foModel.Sum), "", Double.toString(total) });
			} else if (foModel.Type == 2) {
				total -= foModel.Sum;
				csvWriter.writeNext(
					new String[] { foModel.Date.toString(), foModel.Description, "", Double.toString(foModel.Sum), Double.toString(total) });
			}
		}
		csvWriter.writeNext(new String[] { "", "", "", "Total:", Double.toString(total) });
		csvWriter.close();
		return path;
	}

	private void sendMail(String path, int userId) throws AddressException, MessagingException {
		final String username = "noreply.weblibrary@gmail.com";
		final String password = "asdf123~";

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); // TLS

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		UserActions ua = new UserActions();
		String recipient = ua.getUserEmail(userId);
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("noreply.weblibrary@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recipient));
		message.setSubject("History export");
		
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText("Here is your export!");

        // Create a multipart message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(path);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(path);
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);

		Transport.send(message);

		System.out.println("Done");
	}
}
