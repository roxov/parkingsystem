package com.parkit.parkingsystem.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PriceFormatter {

	public double formatPrice (double price) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.HALF_UP);
		String p = df.format(price);
		String p2 = p.replaceAll(",", ".");
		Double.valueOf(p2);
		double formattedPrice = Double.parseDouble(p2);
		
		return formattedPrice ;
	}
}
