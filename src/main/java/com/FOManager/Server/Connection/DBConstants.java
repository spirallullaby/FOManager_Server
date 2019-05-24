package com.FOManager.Server.Connection;

public class DBConstants {
	public static class FOTable {
		public static String name = "finance_operations";
		public static String id = "id";
		public static String user_id = "user_id";
		public static String type = "finance_opration_type";
		public static String sum = "sum";
		public static String description = "description";
		public static String date = "date";
	}
	
	public static class UserTable {
		public static String name = "users";
		public static String id = "id";
		public static String email_address = "email_address";
		public static String password = "password";
	}
}
