package com.parkit.parkingsystem.integration;

import static com.parkit.parkingsystem.constants.ParkingType.BIKE;
import static com.parkit.parkingsystem.constants.ParkingType.CAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)

public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;
	private static ParkingSpotDAO parkingSpotDAO;
	private static ParkingSpot parkingSpot;
	private Ticket ticket;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	/**
	 * Check that a ticket and its data are saved in DB and Parking table is updated
	 * with availability
	 * 
	 * @throws Exception
	 * 
	 */

	@Test
	public void givenInformationForIncomingCar_whenProcessIncomingVehicle_thenVerifyInformationInTicket()
			throws Exception {
		ticket = new Ticket();

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		List<Ticket> ticketsList = ticketDAO.getAllTickets();

		Ticket savedTicket = ticketsList.get(0);
		assertEquals(1, ticketsList.size());
		ParkingSpot savedParkingSpot = savedTicket.getParkingSpot();
		assertEquals(false, savedParkingSpot.isAvailable());
		assertEquals(CAR, savedParkingSpot.getParkingType());
		assertEquals("ABCDEF", savedTicket.getVehicleRegNumber());
		assertEquals(null, savedTicket.getOutTime());
		assertEquals(0, savedTicket.getPrice());

	}

	@Test
	public void givenInformationForIncomingBike_whenProcessIncomingVehicle_thenVerifyInformationInTicket()
			throws Exception {
		ticket = new Ticket();
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		List<Ticket> ticketsList = ticketDAO.getAllTickets();

		Ticket savedTicket = ticketsList.get(0);
		assertEquals(1, ticketsList.size());
		ParkingSpot savedParkingSpot = savedTicket.getParkingSpot();
		assertEquals(false, savedParkingSpot.isAvailable());
		assertEquals(BIKE, savedParkingSpot.getParkingType());
		assertEquals("ABCDEF", savedTicket.getVehicleRegNumber());
		assertEquals(0, savedTicket.getPrice());
		assertNull(savedTicket.getOutTime());

	}

	/**
	 * Test to enter a wrong choice for vehicle type and check that no ticket was
	 * saved.
	 */

	@Test
	public void givenANonExistingParkingType_whenProcessIncomingVehicle_thenNoTicketSaved() {
		when(inputReaderUtil.readSelection()).thenReturn(3);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		List<Ticket> ticketsList = ticketDAO.getAllTickets();
		assertEquals(0, ticketsList.size());
	}

	/**
	 * check that the fare generated and out time are populated correctly in the
	 * database
	 * 
	 * @throws Exception
	 */

	@Test
	public void givenInformationForParkedCar_whenProcessExitingVehicle_thenVerifyInformationInTicket()
			throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		LocalDateTime inTime = LocalDateTime.now();
		Ticket ticket = new Ticket();
		ParkingType parkingType = BIKE;
		int parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
		parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
		parkingSpot.setAvailable(false);
		parkingSpotDAO.updateParking(parkingSpot);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setPrice(0);
		ticket.setInTime(inTime);
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		List<Ticket> ticketsList = ticketDAO.getAllTickets();
		Ticket savedTicket = ticketsList.get(0);
		ParkingSpot savedParkingSpot = savedTicket.getParkingSpot();
		assertEquals(BIKE, savedParkingSpot.getParkingType());
		assertEquals("ABCDEF", savedTicket.getVehicleRegNumber());
		assertEquals(true, savedParkingSpot.isAvailable());
	}

}
