package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * 
 * Configure the connection with the database.
 *
 */

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");
	PreparedStatement ps = null ;
	Connection con = null;
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod", "root", "rootroot");
	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}

	/**
	 * 
	 * @param an SQLrequest from DBConstants
	 * @return the PreparedStatement corresponding to the given SQL request.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	
	public PreparedStatement requestDataBase(String SQLrequest) throws ClassNotFoundException, SQLException {
		con = getConnection();
		ps = con.prepareStatement(SQLrequest);
		return ps;
	}

	public void closeConnectionToDataBase() {
		closeConnection(con);
		closePreparedStatement(ps);
	}

}
