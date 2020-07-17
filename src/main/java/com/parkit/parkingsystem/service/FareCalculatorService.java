package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.DurationCalculator;
import com.parkit.parkingsystem.util.PriceFormatter;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket, boolean regularCustomer) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		DurationCalculator durationCalculator = new DurationCalculator();
		// duration = durationCalculator.calculateDuration(ticket);
		double price = 0;
		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		PriceFormatter priceFormatter = new PriceFormatter();
		// Duration duration = Duration.between(inHour, outHour);
		double duration = (double) (outHour - inHour) / (1000 * 60 * 60);

		if (duration <= 0.5) {
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {

			case CAR: {
				price = duration * Fare.CAR_RATE_PER_HOUR;
				break;
			}
			case BIKE: {
				price = duration * Fare.BIKE_RATE_PER_HOUR;
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

		if (regularCustomer) {
			price = 0.95 * price;
		} else {
		}
		ticket.setPrice(priceFormatter.formatPrice(price));

	}

	public void calculateFareForRegularCustomer(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		PriceFormatter priceFormatter = new PriceFormatter();
		// Duration duration = Duration.between(inHour, outHour);
		double duration = (double) (outHour - inHour) / (1000 * 60 * 60);

		if (duration <= 0.5) {
			ticket.setPrice(0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {

			case CAR: {
				ticket.setPrice(priceFormatter.formatPrice(duration * 0.95 * Fare.CAR_RATE_PER_HOUR));
				break;
			}
			case BIKE: {
				ticket.setPrice(priceFormatter.formatPrice(duration * 0.95 * Fare.BIKE_RATE_PER_HOUR));
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

}