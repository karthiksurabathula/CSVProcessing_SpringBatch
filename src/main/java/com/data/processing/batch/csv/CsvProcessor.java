package com.data.processing.batch.csv;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.data.processing.batch.csv.model.CsvDataModel;
import com.data.processing.batch.csv.model.CsvDataOutput;

@Component
@StepScope
public class CsvProcessor implements ItemProcessor<CsvDataModel, CsvDataOutput> {

	private static final Logger logger = LoggerFactory.getLogger(CsvProcessor.class);

//	@Value("#{jobParameters[filename]}")
	@Value("#{stepExecutionContext['fileName']}")
	String filename;

	@Autowired
	private CsvValidationUtil validationUtil;

	@Override
	public CsvDataOutput process(CsvDataModel data) throws Exception {

		long start = System.nanoTime();

		boolean valid = false;

		String inputString = String.format("%s,%s,%s,%s,%s,%s,%s", data.getCustomerId(), data.getTransactionId(),
				data.getAccountType(), data.getPortfolioId(), data.getEmailAddress(), data.getDate(),
				data.getChecksum());

		if (validationUtil.checkCustomerId(data.getCustomerId())) {
			if (validationUtil.checkTransaction_Id(data.getTransactionId())) {
				if (validationUtil.checkIfAccount_type(data.getAccountType())) {
					if (validationUtil.checkPortfolio_Id(data.getPortfolioId())) {
						if (validationUtil.checkEmail_Address(data.getEmailAddress())) {
							if (validationUtil.checkDate(data.getDate())) {

								String fixString = String.format("%s%s%s%s%s%s", data.getCustomerId(),
										data.getTransactionId(), data.getAccountType(), data.getPortfolioId(),
										data.getEmailAddress(), data.getDate());

								if (validationUtil.checkFixChecksum(fixString, data.getChecksum())) {
									valid = true;
								}
							}
						}
					}
				}
			}
		}

		long end = System.nanoTime();
		long elapsedTime = end - start;
		logger.debug("elapsedTime: " + elapsedTime + "................");

		if (valid) {
			if (filename.equals("NA")) {
				return new CsvDataOutput(inputString, true, DigestUtils.sha512Hex(inputString),
						data.getResource().getFilename());
			} else {
				return new CsvDataOutput(inputString, true, DigestUtils.sha512Hex(inputString), filename);
			}

		} else {
			if (filename.equals("NA")) {
				return new CsvDataOutput(inputString, false, DigestUtils.sha512Hex(inputString),
						data.getResource().getFilename());
			} else {
				return new CsvDataOutput(inputString, false, DigestUtils.sha512Hex(inputString), filename);
			}
		}

	}

}
