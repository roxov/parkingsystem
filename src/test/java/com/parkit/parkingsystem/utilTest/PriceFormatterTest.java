package com.parkit.parkingsystem.utilTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.PriceFormatter;

public class PriceFormatterTest {
	
	@Test
	public void testDoubleWithTwoDecimals () {
		PriceFormatter priceFormatter = new PriceFormatter () ;
		double price = 2.5987;
		double a = 2.60;
		
		priceFormatter.formatPrice(price);
		
		assertEquals(a, priceFormatter.formatPrice(price));
	}
}
