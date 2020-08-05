package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FullParkingException extends Exception {
	private static final Logger logger = LogManager.getLogger("ParkingService");

	public FullParkingException() {
		logger.error("Error fetching next available parking slot");
	}
}
