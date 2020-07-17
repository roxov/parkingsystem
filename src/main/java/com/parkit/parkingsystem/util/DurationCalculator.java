package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.model.Ticket;

public class DurationCalculator {
	public double calculateDuration (Ticket ticket) {
		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		
		// Duration duration = Duration.between(inHour, outHour);
		double duration = (double) (outHour - inHour) / (1000 * 60 * 60);
		//double duration = 0;
		return duration;
	}
}
