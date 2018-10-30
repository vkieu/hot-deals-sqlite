package com.futuretex.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import com.futuretex.domain.Product;
import org.apache.log4j.Logger;

public class ProductHelper {

	final static Logger LOG = Logger.getLogger(ProductHelper.class);


	public static String productsToString(Collection<Product> products) {
		final StringBuilder sb = new StringBuilder();
		for (Product p : products) {
			sb.append(p).append("\n");
		}
		return sb.toString();
	}

	public static String productsToHTML(Collection<Product> products) {

		DateFormat df = new SimpleDateFormat("dd MMM yy");

		final StringBuilder sb = new StringBuilder("<html><table style='border:1px solid black;'>");
		sb.append("<tr>")
				//.append("<th>Id</th>")
				.append("<th bgcolor='#FF0000' width='70%'>Title</th>")
				.append("<th bgcolor='#FF0000'>Price</th>")
				.append("<th bgcolor='#FF0000'>Discount</th>")
				.append("<th bgcolor='#FF0000'>Misc</th>")
				.append("<th bgcolor='#FF0000'>Link</th>") //image
				.append("</tr>");

		for (Product p : products) {
			sb.append("<tr>")
					.append("<td style='text-align:left;border-bottom: 1px solid #ddd;'>")
					.append("<a href='" + p.getDetailedLink() + "' target='_new'>")
					.append(p.getTitle())
					.append("</a>")
					.append("<div>").append(p.getDescription()).append("</div>")
					.append("</td>")
					.append("<td style='text-align:left;border-bottom: 1px solid #ddd;'>" + p.getPriceString() + "</td>")
					.append("<td style='text-align:left;border-bottom: 1px solid #ddd;'>"
							+ (p.getPercentageDiscount() == null ? "&nbsp;" : p.getDiscountString()) + "</td>")
					.append("<td style='text-align:left;border-bottom: 1px solid #ddd;'>"
							+ "<br/>Comment: " + p.getNumberOfComments()
							+ "<br/>, Vote: " + p.getVoteString()
							+ (p.getDateFound() == null ? "&nbsp;" : "<br/>, Found: " + df.format(p.getDateFound())) +
							"</td>")
					.append("<td style='text-align:left;border-bottom: 1px solid #ddd;'>" +
							"<a href='" + p.getDetailedLink() + "'>" +
							"<img src='" + p.getImageLink() + "' style='width:100px;height:100px;'/></a>" +
							"</td>")
					.append("</tr>");
		}
		sb.append("</table></html>");
		return sb.toString();
	}
}
