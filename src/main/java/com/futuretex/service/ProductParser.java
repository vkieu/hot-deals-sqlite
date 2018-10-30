package com.futuretex.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.futuretex.domain.Product;
import com.futuretex.utils.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProductParser {

	final static Logger LOG = Logger.getLogger(ProductParser.class);

	private static String parseExpiredDate(Element e) {
		Elements elements = e.select("span.hide--toW2 > span.text--b");
		if (elements != null && elements.first() != null) {
			LOG.info("parseExpiredDate " + elements.first().text());
			return elements.first().text();
		}
		return null;
	}

	private static void parsePercentageDiscount(Element e, Product p) {
		Elements elements = e.select("span.flex--inline > span.space--ml-1");
		if (elements != null && elements.first() != null) {
			String discountString = elements.first().text();
			LOG.debug("parsePercentageDiscount " + discountString);
			if (!StringUtils.isEmpty(discountString)) {
				p.setDiscountString(discountString);
				if (!discountString.contains("FREE")) {
					final String text = discountString.substring(0, discountString.length() - 1);
					if (!StringUtils.isEmpty(text)) {
						p.setPercentageDiscount(Double.parseDouble(text));
					}
				}
			}
		}
	}

	private static int parseCommentCount(Element e) {
		Elements elements = e.select("a.cept-comment-link > span");
		if (elements != null) {
			return Integer.parseInt(elements.first().text());
		}
		return 0;
	}


	private static void parsePrice(Element e, Product p) {
		String priceString = e.getElementsByClass("thread-price").text();
		if (!StringUtils.isEmpty(priceString)) {
			p.setPriceString(priceString);
			String newStr = priceString.replaceAll("[^\\d.]+", "");
			try {
				p.setPrice(new BigDecimal(newStr));
			} catch (NumberFormatException ex) {
				LOG.error("Error parsePrice " + priceString);
				p.setPrice(BigDecimal.valueOf(ProductFilter.MAX_PRICE - 1));
			}
		} else {
			p.setPrice(BigDecimal.ZERO);
		}
	}

	static int parseTemperature(String tempertureString) {
		if (tempertureString != null && tempertureString.indexOf("°") > -1) {
			return Integer.parseInt(tempertureString.substring(0, tempertureString.indexOf("°")));
		}
		return 0;
	}

	static boolean parseExpired(String expiredString) {
		if (expiredString != null) {
			return expiredString.toLowerCase().contains("expired");
		}
		return false;
	}

	static Long parseAgo(String agoString) {
		LOG.debug("parseAgo called. " + agoString);
		long result = System.currentTimeMillis();//now
		if (agoString.indexOf(" h,") > -1) {
			String hourString = agoString.substring(Math.max(agoString.indexOf(" h,") - 2, 0), agoString.indexOf(" h,")).trim();
			result -= (Integer.parseInt(hourString) * 1000 * 60 * 60);
		}

		if (agoString.indexOf(" m") > -1) {
			String minuteString = agoString.substring(Math.max(agoString.indexOf(" m") - 2, 0), agoString.indexOf(" m")).trim();
			result -= (Integer.parseInt(minuteString) * 1000 * 60);
		}

		if (agoString.indexOf(" s") > -1) {
			String secondString = agoString.substring(Math.max(agoString.indexOf(" s") - 2, 0), agoString.indexOf(" s")).trim();
			result -= (Integer.parseInt(secondString) * 1000);
		}
		return result;
	}

	static Long parseDate(String dateString) {
		try {
			String format = "dd/mm/yyyy";
			DateFormat df = new SimpleDateFormat(format);
			return df.parse(dateString).getTime();
		} catch (ParseException e) {
			LOG.error("error parseDate" + dateString);
			return System.currentTimeMillis();
		}
	}

	static Long parseThisYearDate(String dateString) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			String d = dateString
					.replace("th", "")
					.replace("st", "")
					.replace("nd", "")
					.replace("rd", "")
					+ year;
			String format = "dd MMMyyyy";
			DateFormat df = new SimpleDateFormat(format);
			return df.parse(d).getTime();
		} catch (ParseException e) {
			LOG.error("error parseThisYearDate: " + dateString);
			return 0L;
		}
	}

	private static Long parseDateFound(Element e) {
		Elements elements = e.select("span.metaRibbon > span.hide--fromW3");
		String foundString = elements.last().text();
		if (foundString.contains("Shipping")) {
			foundString = elements.first().text();
		}
		LOG.debug("foundString?" + foundString);
		foundString = foundString.
				replace("Refreshed ", "").
				replace("Made hot ", "").
				replace("Found", "").
				replace("Updated", "");
		if (foundString.contains("ago")) {
			return parseAgo(foundString);
		} else if (foundString.contains("TODAY") || foundString.contains("Local")) {
			return System.currentTimeMillis();
		} else if (foundString.contains("/")) {
			return parseDate(foundString);
		} else {
			return parseThisYearDate(foundString);
		}
	}

	private static String parseMerchantLink(Element e) {
		Elements elements = e.select("a.cept-merchant-link");
		if (elements != null && elements.first() != null) {
			LOG.debug("parseMerchantLink" + elements.first().attr("href"));
			return elements.first().attr("href");
		}

		return null;
	}

	private static String parseDealLink(Element e) {
		Elements elements = e.select("a.cept-dealBtn");
		if (elements != null && elements.first() != null) {
			return elements.first().attr("href");
		}
		return null;
	}


	public static Product parseListElement(Element e) {

		String title = e.getElementsByClass("thread-title--list").first().text();
		String id = Utility.md5(title);

		Product product = new Product(id, title);

		String description = e.getElementsByClass("cept-description-container").first().text();

		String detailedLink = e.select("a.thread-link").first().attr("href");
		String voteString = e.getElementsByClass("vote-box").first().text();

		product.setDescription(description);
		product.setDetailedLink(detailedLink);

		product.setVoteString(voteString);
		product.setTemperature(parseTemperature(voteString));
		product.setExpired(parseExpired(voteString));
		product.setNumberOfComments(parseCommentCount(e));
		product.setMerchantLink(parseMerchantLink(e));
		product.setDealLink(parseDealLink(e));
		product.setExpiredDate(parseExpiredDate(e));
		product.setDateFound(parseDateFound(e));

		parsePrice(e, product);
		parsePercentageDiscount(e, product);

		String imageLink = e.select("img.thread-image").first().attr("src");
		if (StringUtils.isEmpty(imageLink)) {
			LOG.debug(e.select("img.thread-image").attr("data-lazy-img"));
			String imgLinkJson = e.select("img.thread-image").attr("data-lazy-img");
			LOG.debug("imgLinkJson? " + imgLinkJson);
			try (JsonReader reader = Json.createReader(new StringReader(imgLinkJson))) {
				JsonObject jsonObject = reader.readObject();
				imageLink = jsonObject.getString("src");
			}
		}
		product.setImageLink(imageLink);

		return product;
	}

}