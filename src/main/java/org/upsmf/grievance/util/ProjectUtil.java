/**
 *
 */
package org.upsmf.grievance.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class ProjectUtil {
	/*
	 * projectUtil ProjectUtil instance
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(ProjectUtil.class);
	private static ProjectUtil projectUtil = null;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final Random random = new Random();
	private static final String ENCOUNTERED_AN_EXCEPTION = "Encountered an Exception :  %s";

	/**
	 * Default constructor
	 * 
	 * @throws Exception
	 */
	private ProjectUtil() throws Exception {
		if (projectUtil != null) {
			throw new Exception();
		}
	}

	static {
		try {
			projectUtil = getProjectUtilInstance();
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		Pattern.compile(EMAIL_PATTERN);
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 * @throws RuntimeException
	 */
	public static synchronized ProjectUtil getProjectUtilInstance() throws Exception {
		if (projectUtil == null) {
			projectUtil = new ProjectUtil();
		}
		return projectUtil;
	}

	/**
	 * this method is used to print class name and line number
	 *
	 * @param object
	 * @return
	 */
	public String getClassNameAndLineNo(Object object) {
		String message = "";
		message = " " + object.getClass().getName() + "********"
				+ Thread.currentThread().getStackTrace()[2].getLineNumber();
		return message;
	}

	/**
	 * this method will generate some random password. which is dog name and is
	 * suggested by client.
	 *
	 * @return String password.
	 */
	public static String generatePassword() {
		String[] passwords = new String[] { "" };

		int index = random.nextInt(passwords.length);

		return passwords[index];
	}

	/**
	 * this method is used to add fare and tip amount
	 *
	 * @param fare
	 *            fare amount
	 * @param tip
	 *            tip amount
	 * @return result
	 */
	public static double addDoubles(String fare, String tip) {
		double fareAmount = 0;
		double tipAmount = 0;
		if (fare != null && !"".equals(fare)) {
			fareAmount = Double.parseDouble(fare.trim());
		}
		if (tip != null && !"".equals(tip)) {
			tipAmount = Double.parseDouble(tip);
		}
		return fareAmount + tipAmount;
	}

	/**
	 * this method is used to subtract fare and tip amount
	 *
	 * @param fare
	 *            fare amount
	 * @param tip
	 *            tip amount
	 * @return result
	 */
	public static double substractDoubles(String fare, String tip) {

		double fareAmount = Double.parseDouble(fare);
		double tipAmount = Double.parseDouble(tip);
		boolean order = fareAmount - tipAmount > 0;

		String bigger = fareAmount - tipAmount > 0 ? fare : tip;
		String smaller = fareAmount - tipAmount < 0 ? fare : tip;

		int bigDollars = bigger.contains(".") ? Integer.parseInt(bigger.split("\\.")[0]) : Integer.parseInt(bigger);
		int smallDollars = smaller.contains(".") ? Integer.parseInt(smaller.split("\\.")[0])
				: Integer.parseInt(smaller);

		int bigCents = bigger.contains(".") ? Integer.parseInt(bigger.split("\\.")[1]) : 0;
		int smallCents = smaller.contains(".") ? Integer.parseInt(smaller.split("\\.")[1]) : 0;
		if (smallCents < 10 && smallCents != 0) {
			smallCents = smallCents * 10;
		}
		int totalCents = bigCents - smallCents;
		int extraDollar = 0;
		int cents = totalCents;

		if (totalCents < 0) {
			extraDollar = -1;
			cents = 100 + bigCents - smallCents;
		}
		int dollars = bigDollars - smallDollars + extraDollar;
		if (!order) {
			dollars = dollars * -1;
		}

		String totalAmount = dollars + "." + cents;
		if (cents < 10) {
			totalAmount = dollars + ".0" + cents;
		}
		return Double.parseDouble(totalAmount);
	}

	/**
	 * this method is used to generate user session id.
	 *
	 * @param email
	 *            user email id.
	 * @param roleId
	 *            user role id.
	 * @param source
	 *            String
	 * @return String session id value.
	 */
	public static String getUniqueId(String email, long roleId, String source) {
		email = email.replace('.', 'D');
		email = email.replace('@', 'L');
		email = email.replace("com", "se");
		email = email.replace("in", "xp");
		StringBuilder builder = new StringBuilder();
		String str = System.currentTimeMillis() + random.nextInt() + "";
		byte[] data = { '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', '4', 'i', 'j', 'k', '5', 'l', 'm', 'n', 'o', 'p',
				'q', '6', 'A', 'B', 'C', 'D', '9', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', '8', 'M', 'N', 'O', 'P', 'Q',
				'R', '7', 'S', 'T', 't', 'u', 'U', 'V', 'v', 'W', 'w', 'X', 'x', 'Y', 'y', 'Z', 'z', '1', '2', '3' };
		String value = random.nextInt() + "";
		builder.append(value.substring(1, value.length() / 2));
		builder.append(email.substring(3, email.length() / 2));
		builder.append(str.substring(str.length() - 2) + "" + str.substring(0, str.length() - 2));
		builder.append(email.substring(email.length() - 4));
		for (int i = 0; i < 4; i++) {
			builder.append(random.nextInt(data.length));
		}
		builder.append("#" + source + "#" + roleId * random.nextInt(random.nextInt(data.length)));
		return builder.toString();
	}

	/**
	 * This method will check incoming request data contains that key or not.
	 *
	 * @param requestData
	 *            requested data.
	 * @param key
	 *            String
	 * @return true/false
	 */
	public static boolean isKeyFound(JsonNode requestData, String key) {
		return (requestData.has(key));
	}

	public static String formatPhoneNumber(String phone) {
		String phoneNumber = phone;
		StringBuilder builder = new StringBuilder(phone);
		if (phone != null && phone.trim().length() == 10) {
			phoneNumber = "(" + builder.substring(0, 3) + ") " + builder.substring(3, 6) + "-" + builder.substring(6);
		}
		return phoneNumber;
	}

	/**
	 * This method will remove user phone number format from (xxx) xxx-xxxx to
	 * xxxxxxxxxx format.
	 *
	 * @param phone
	 *            String
	 * @return String
	 */
	public static String removePhoneNumberFormat(String phone) {
		if (phone != null && phone.trim().length() > 10) {
			phone = phone.replace("(", "").replace(") ", "").trim().replace("-", "");
		}
		return (phone != null) ? phone.trim() : "";

	}

	/**
	 * This method will check user phone number it should not empty , null and
	 * length should be 10 digits and it should contain all numeric value.
	 *
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumbervalid(String phoneNumber) {
		boolean response = true;
		if (phoneNumber == null || "".equals(phoneNumber) || phoneNumber.trim().length() < 10
				|| "0000000000".equals(phoneNumber.trim())) {
			response = false;
		} else {
			try {
				Long.parseLong(phoneNumber.trim());
			} catch (Exception e) {
				response = false;
			}
		}
		return response;
	}

	/**
	 * This method will check provided string is null or empty if string is null or
	 * empty then it will provide true other wise false.
	 *
	 * @param value
	 *            String
	 * @return boolean
	 */
	public static boolean isStringNullOrEmpty(String value) {
		return (value == null || "".equals(value.trim()) || "null".equals(value.trim()));
	}

	public static boolean isEmailValid(String email) {
		boolean response = false;
		if (ProjectUtil.isStringNullOrEmpty(email)) {
			return response;
		}
		String[] emails = email.split("@");
		try {
			String[] splitedVal = emails[1].split("[.]");
			if ("gmail".equalsIgnoreCase(splitedVal[0]) || "yahoo".equalsIgnoreCase(splitedVal[0])
					|| "rediffmail".equalsIgnoreCase(splitedVal[0]) || "hotmail".equalsIgnoreCase(splitedVal[0])
					|| "AOL".equalsIgnoreCase(splitedVal[0])) {
				if ("com".equalsIgnoreCase(splitedVal[1])) {
					response = true;
				}
			} else {
				response = true;
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return response;
	}

	/**
	 * This method will calculate percentage(4.75) of provided amount.
	 *
	 * @param amount
	 *            double
	 * @param charge
	 *            doubel
	 * @return double
	 */
	public static double calculatePercentage(double amount, double charge) {
		return ((amount * charge) / 100);
	}

	/**
	 * This method will crate sub string of provided String based on count.
	 *
	 * @param val
	 *            String
	 * @param count
	 *            int
	 * @return String
	 */
	public static String cutOffStringSize(String val, int count) {
		if (val == null) {
			return val;
		}
		val = val.trim();
		if (val.length() > count) {
			val = val.substring(0, count);
			val = val + "...";
		}
		return val;
	}

	public static String getRandomStringVal() {
		String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= 8; i++) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();

	}

	/**
	 * This method will return random int value.
	 *
	 * @param lower
	 *            int
	 * @param upper
	 *            int
	 * @return int
	 */
	private static int getRandom(int lower, int upper) {
		return (random.nextInt() * (upper - lower)) + lower;
	}

	/**
	 * This method receives the minutes and converts it into hours in 00H:00M format
	 *
	 * @param minutes
	 * @return String
	 */
	public static String convertMinutesToHours(long minutes) {
		String startTime = "00:00";
		long h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
		long m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
		return h + "H:" + m + "M";
	}

	public static String[] getStartAndEndDatForMonth(String date) {
		String startDate = date + "-01";
		String[] ymd = startDate.split("-");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, Integer.parseInt(ymd[1]));
		cal.set(Calendar.YEAR, Integer.parseInt(ymd[0]));
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		String endDate = date + "-" + cal.get(Calendar.DATE);
		String[] dates = new String[2];
		dates[0] = startDate;
		dates[1] = endDate;
		return dates;

	}

	public static int[] getMonthAndYearFromDate(String dateString) {
		int[] calender = new int[2];
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		try {
			Date date = formatter.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			calender[0] = month + 1;
			calender[1] = year;

		} catch (ParseException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return calender;
	}

	public static Date getFullDate(String dateString) {
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return date;
	}

	/**
	 * This method gets the complete file name
	 *
	 * @param fileName
	 * @return
	 */

	public static String getAppendedDocumentName(String fileName) {
		long timestamp = System.currentTimeMillis();
		String[] splitFileName = fileName.split("\\.");
		String fileExtenstion = splitFileName[(splitFileName.length) - 1];
		splitFileName[splitFileName.length - 1] = "";
		StringBuilder builder = new StringBuilder();
		for (String stringPart : splitFileName) {
			builder.append(stringPart);
		}
		return builder.toString() + "_" + Long.toString(timestamp) + "." + fileExtenstion;

	}

	public static boolean validateEmail(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public static boolean validateDateFormat(String date) {
		try {
			return (date.matches("((?:19|20)\\d\\d)-(0?[1-9]|1[012])-([12][0-9]|3[01]|0?[1-9])"));
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isObjectListNullOrEmpty(List<? extends Object> objectList) {
		return (objectList == null) || (objectList.isEmpty());
	}

	public static boolean isObjectNull(Object object) {
		return (object == null);
	}

}
