package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

/**
 * 
 * Test different cases of price calculation
 *
 */

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	public static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void giveOneHourParkingTimeForACar_whenCalculateFare_thenCalculateCarFareForAnHour() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusHours(1);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void giveOneHourParkingTimeForABike_whenCalculateFare_thenCalculateBikeFareForAnHour() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusHours(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void givenANullTypeVehicle_whenCalculateFare_thenThrowNullPointerException() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusHours(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
	}

	@Test
	public void givenABikeWithFutureInTime_whenCalculateFare_thenThrowException() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.plusHours(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
	}

	@Test
	public void givenACarWithNullOutTime_whenCalculateFare_thenThrowException() {
		LocalDateTime inTime = LocalDateTime.now();
		LocalDateTime outTime = null;
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(Exception.class, () -> fareCalculatorService.calculateFare(ticket, false));
	}

	@Test
	public void givenLessThanOneHourParkingTimeForBike_whenCalculateFare_thenGiveFortyFiveMinutesBikeFare() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(45);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals((double) Math.round(Fare.BIKE_RATE_PER_HOUR * 0.75 * 100) / 100, ticket.getPrice());
	}

	@Test
	public void givenLessThanOneHourParkingTimeForCar_whenCalculateFare_thenGiveFortyFiveMinutesCarFare() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(45);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals((double) Math.round(Fare.CAR_RATE_PER_HOUR * 0.75 * 100) / 100, ticket.getPrice());
	}

	@Test
	public void givenMoreThanADayParkingTimeForCar_whenCalculateFare_thenGiveTwentyFourHoursCarFare() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusDays(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals((double) Math.round(Fare.CAR_RATE_PER_HOUR * 24 * 100) / 100, ticket.getPrice());
	}

	@Test
	public void givenLessThanThirtyMinutesParkingTimeForCar_whenCalculateFare_thenGiveFreeFare() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(29);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, false);
		assertEquals(0, ticket.getPrice());
	}

	@Test
	public void givenLessThanOneHourParkingTimeForReccurentCostumersBike_whenCalculateFare_thenGiveFortyFiveMinutesFareWithFivePercentDiscount() {
		LocalDateTime outTime = LocalDateTime.now();
		LocalDateTime inTime = outTime.minusMinutes(45);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, true);
		assertEquals((double) Math.round(Fare.BIKE_RATE_PER_HOUR * 0.75 * 0.95 * 100) / 100, ticket.getPrice());
	}

}
