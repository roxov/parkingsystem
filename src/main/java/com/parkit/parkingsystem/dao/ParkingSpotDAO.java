package com.parkit.parkingsystem.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

/**
 * 
 * Update the parking spots when a vehicle enter or exit the parking.
 *
 */

public class ParkingSpotDAO {
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");
	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * 
	 * @param the parkingType given by the costumer.
	 * @return the parkingSpot to park where.
	 */
	
	public int getNextAvailableSlot(ParkingType parkingType) {
		int result = -1;
		try {
			PreparedStatement ps = dataBaseConfig.requestDataBase(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
				;
			}
			dataBaseConfig.closeResultSet(rs);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnectionToDataBase();
		}
		return result;
	}

	/**
	 * 
	 * @param the parkingSpot where the vehicle was parked.
	 * @return a boolean to indicate if the updating is done.
	 */
	
	public boolean updateParking(ParkingSpot parkingSpot) {
		// update the availability for that parking slot
		try {
			PreparedStatement ps = dataBaseConfig.requestDataBase(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
			dataBaseConfig.closeConnectionToDataBase();
		}
	}

}
