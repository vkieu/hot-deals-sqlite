package com.futuretex.service;

import com.futuretex.domain.Product;
import org.apache.commons.lang3.StringUtils;

public class ProductFilter {

	private static String[] IGNORE_KEYWORDS = {"food", "now tv", "HARIBO"};

	private static final double MIN_PRICE = 50.0d;
	public static final double MAX_PRICE = 10000d;
	private static final int INTERESTED_TEMPERATURE = 50;

	private int expiredCount = 0;
	private int underPriceCount = 0;
	private int overPriceCount = 0;
	private int applyCallCount = 0;
	private int matchedCount = 0;
	private int ignoredKeywordCount = 0;
	private int highInterestCount = 0;
	private int lowInterestCount = 0;

	public ProductFilter() {
	}

	public boolean apply(Product product) {
		applyCallCount++;

		if (product.getExpired()) {
			expiredCount++;
			return false;
		}
		if (hasIgnoredKeywords(product.getTitle())) {
			ignoredKeywordCount++;
			return false;
		}

		if (!StringUtils.isEmpty(product.getPriceString())) {
			if (product.getPrice().doubleValue() < MIN_PRICE) {
				underPriceCount++;
				return false;
			}
			if (product.getPrice().doubleValue() > MAX_PRICE) {
				overPriceCount++;
				return false;
			}
		} else if (product.getTemperature() > INTERESTED_TEMPERATURE) {
			highInterestCount++;
			//accept
		} else if (product.getTemperature() < INTERESTED_TEMPERATURE) {
			lowInterestCount++;
			return false;
		}

		matchedCount++;
		return true;
	}

	private boolean hasIgnoredKeywords(String theString) {
		for (String keyword : IGNORE_KEYWORDS) {
			if (theString.toLowerCase().contains(keyword.toLowerCase())) {
				return true;
			}
		}
		return false;
	}


	public String getSummary() {
		return "Filter(s) summary: \n" +
				"Total products found: " + applyCallCount + "\n" +
				expiredCount + " expired\n" +
				underPriceCount + " product(s) under the cost of £" + MIN_PRICE + "\n" +
				overPriceCount + " product(s) over the cost of £" + MAX_PRICE + "\n" +
				ignoredKeywordCount + " excluded due to keyword filter\n" +
				lowInterestCount + " excluded due no price and low interest\n" +
				matchedCount + " product(s) matched.\n"
				;
	}
}
