package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.LocalDateTime;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.PriceFormatter;

/**
 * 
 * Calculate the fare according to the duration and to the recurring accesses of
 * the costumer.
 *
 */

public class FareCalculatorService {

	/**
	 * Calculate fares with the following conditions : - No fare for a vehicle which
	 * was parked for less than 30 minutes; - 5% discount for recurrent costumers
	 * 
	 * @param ticket
	 * @param a      boolean to know if it's a recurrent customer
	 */

	public void calculateFare(Ticket ticket, boolean regularCustomer) {

		if ((ticket.getOutTime() == null) || ((ticket.getOutTime().isBefore(ticket.getInTime())))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double price = 0;
		PriceFormatter priceFormatter = new PriceFormatter();

		// long inHour = ticket.getInTime().getTime();
		// long outHour = ticket.getOutTime().getTime();
		// double duration = (double) (outHour - inHour) / (1000 * 60 * 60);

		// Récupère le temps sous forme LocalDateTime au lieu de Timestamp
		LocalDateTime inHour = ticket.getInTime();
		LocalDateTime outHour = ticket.getOutTime();
		long duration = Duration.between(inHour, outHour).toMinutes();

		if (duration > 30) {
			switch (ticket.getParkingSpot().getParkingType()) {

			case CAR: {
				price = Fare.CAR_RATE_PER_HOUR;
				break;
			}
			case BIKE: {
				price = Fare.BIKE_RATE_PER_HOUR;
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown Parking Type");
			}
			price = price * (duration / 60d);
		}

		if (regularCustomer) {
			System.out.println(
					"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
			price = price * 0.95;
		}

		ticket.setPrice(priceFormatter.formatPrice(price));

	}

}