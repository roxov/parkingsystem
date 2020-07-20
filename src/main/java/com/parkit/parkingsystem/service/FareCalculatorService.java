package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.PriceFormatter;

/**
 * 
 * Calculate the fare according to the duration and to the recurring accesses of the costumer.
 *
 */

public class FareCalculatorService {

	/**
	 * Calculate fares with the following conditions :
	 * - No fare for a vehicle which was parked for less than 30 minutes;
	 * - 5% discount for recurrent costumers
	 *  
	 * @param ticket
	 * @param a boolean to know if it's a recurrent customer
	 */
	
	public void calculateFare(Ticket ticket, boolean regularCustomer) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
		//if ((ticket.getOutTime() == null) || ((ticket.getOutTime().isBefore(ticket.getInTime())))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double price = 0;
		/*
		LocalDateTime inHour = ticket.getInTime();
		LocalDateTime outHour = ticket.getOutTime();
		// CONVERTIR UN LOCALDATETIME EN LONG POUR LA DUREE (between prend un temporal)
		//The method between(Temporal, Temporal) in the type Duration is not applicable for the arguments (LocalDateTome, LDT)
		Duration duration = Duration.between(inHour, outHour);
		*/
		
		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		PriceFormatter priceFormatter = new PriceFormatter();
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
			System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
			price = 0.95 * price;
		} else {
		}
		ticket.setPrice(priceFormatter.formatPrice(price));

	}


}