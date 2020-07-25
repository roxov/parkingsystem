package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * Handle the tickets content.
 *
 */

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");
	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * 
	 * @param ticket
	 * @return a boolean to indicate if the saving is done.
	 */

	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error saving ticket", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;

	}

	/**
	 * 
	 * @param vehicleRegNumber, given by the costumer.
	 * @return the corresponding ticket.
	 */

	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				retrieveTicket(ticket, rs);
				ticket.setVehicleRegNumber(vehicleRegNumber);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error getting ticket", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	/**
	 * 
	 * @param vehicleRegNumber, given by the costumer
	 * @return a container Optional with a ticket if it's a recurrent costumer or an
	 *         Optional.isEmpty().
	 */

	public Optional<Ticket> getPreviousRegistration(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_A_PREVIOUS_REGISTRATION);
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				retrieveTicket(ticket, rs);
				ticket.setVehicleRegNumber(vehicleRegNumber);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error getting previous registration", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return Optional.ofNullable(ticket);
	}

	/**
	 * 
	 * @return all the tickets saving in the database.
	 */

	public List<Ticket> getAllTickets() {
		Connection con = null;
		Ticket ticket = null;
		List<Ticket> ticketsList = new ArrayList<>();
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_ALL_TICKETS);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ticket = new Ticket();
				retrieveTicket(ticket, rs);
				ticket.setVehicleRegNumber(rs.getString(7));
				ticketsList.add(ticket);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error getting all tickets", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticketsList;
	}

	/**
	 * 
	 * @param vehicleRegNumber, given by the costumer.
	 * @return an Optional with a ticket if a vehicle with the same registration
	 *         number is already parked, or an Optional.isEmpty() if not.
	 */

	public Optional<Ticket> getExistingParkedVehicleTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_PARKED_CAR_TICKET);
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				retrieveTicket(ticket, rs);
				ticket.setVehicleRegNumber(vehicleRegNumber);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error getting parked vehicles", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return Optional.ofNullable(ticket);
	}

	/**
	 * 
	 * @param ticket
	 * @return a boolean to indicate if the updating was done.
	 */

	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * 
	 * Configure the ticket with principal datas.
	 * 
	 * @param ticket
	 * @param the    Resultset rs of the SQL request.
	 * @throws SQLException
	 */

	public void retrieveTicket(Ticket ticket, ResultSet rs) throws SQLException {
		ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(rs.getInt(2));
		ticket.setPrice(rs.getDouble(3));
		ticket.setInTime(rs.getTimestamp(4));
		ticket.setOutTime(rs.getTimestamp(5));
	}

}
