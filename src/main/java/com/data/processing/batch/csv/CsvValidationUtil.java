package com.data.processing.batch.csv;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CsvValidationUtil {

	private static final Logger logger = LoggerFactory.getLogger(CsvValidationUtil.class);

	@Value("${batch.csv.validation.checkCustomerId_regex}")
	String checkCustomerId_regex; // = "[0-9]{6}";
	@Value("${batch.csv.validation.checkTransaction_Id_regex}")
	String checkTransaction_Id_regex;// = "[a-zA-Z0-9]{16}";
	@Value("${batch.csv.validation.portifolio_Id_regex}")
	String portfolio_Id_regex; // = "[A-Za-z]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}";
	@Value("${batch.csv.validation.email_Address_regex}")
	String email_Address_regex; // = "^[a-zA-Z]+([0-9]?)+[._-][a-zA-Z]+@\\W*((?i)(lseg|refinitiv)(?-i))\\W*.\\W*((?i)(com|net|eu)(?-i))\\W*";
	@Value("${batch.csv.validation.account_types}")
	String[] account_types; // = { "Equity Demat", "Derivatives Trading", "Commodity Demat", "Commodity Trading", "Discount Broking", "Full-service Trading" };
	@Value("${batch.csv.validation.dateFormat}")
	String dateFormatt;
	@Value("${batch.csv.validation.years_valid}")
	int[] years_valid;
	@Value("${batch.csv.validation.months_valid}")
	int[] month_valid;


	public boolean checkCustomerId(String customer_id) {
		Pattern regexPattern = Pattern.compile(checkCustomerId_regex);
		Matcher matcher = regexPattern.matcher(customer_id);
		try {
			int a = Integer.parseInt(customer_id);
			return matcher.find() && (a >= 100000 && a <= 999999);
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return false;
		}
	}

	public boolean checkTransaction_Id(String transaction_Id) {

		Pattern regexPattern = Pattern.compile(checkTransaction_Id_regex);
		Matcher matcher = regexPattern.matcher(transaction_Id);
		return matcher.find();
	}

	public boolean checkIfAccount_type(String account_type) {
		return Arrays.stream(account_types).anyMatch(account_type::contains);
	}

	public boolean checkPortfolio_Id(String portfolio_Id) {
		Pattern regexPattern = Pattern.compile(portfolio_Id_regex);
		Matcher matcher = regexPattern.matcher(portfolio_Id);
		return matcher.find();
	}

	public boolean checkEmail_Address(String email_Address) {
		Pattern regexPattern = Pattern.compile(email_Address_regex);
		Matcher matcher = regexPattern.matcher(email_Address);
		return matcher.find();
	}

	public boolean checkDate(String date) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateFormatt);
		try {
			LocalDate zdt = LocalDate.parse(date, dateFormat);
			YearMonth ym = YearMonth.from(zdt);
			int year = ym.getYear();
			int month = ym.getMonthValue();

			if ((Arrays.stream(years_valid).anyMatch(i -> i == year))
					&& (Arrays.stream(month_valid).anyMatch(i -> i == month))) {
				return true;
			}

//			if ((year == 2007 || year == 2008 || year == 2009) && (month == 1 || month == 12)) {
//				return true;
//			}
			return false;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			return false;
		}
	}

	// https://gigi.nullneuron.net/gigilabs/calculating-the-checksum-of-a-fix-message/
	public boolean checkFixChecksum(String checkString, String checksum) {
		byte total = 0;
		checkString = checkString.replace('|', '\u0001');
		Charset characterSet = Charset.forName("US-ASCII");
		byte[] messageBytes = checkString.getBytes(characterSet);

		for (int i = 0; i < checkString.length(); i++) {
			total += messageBytes[i];
		}

		logger.debug("" + (total % 256) + " : " + checksum + " = " + checkString);

		if ((total % 256) == Integer.parseInt(checksum)) {
			return true;

		} else {
			return false;
		}
	}
}
