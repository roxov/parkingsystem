package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private static FareCalculatorService fareCalculatorService;
    
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
           

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }
    
   
    @Test
    public void testToEnterWithAnExistingRegNumberThenANewOne() throws Exception {
    	when(ticketDAO.getExistingParkedVehicleTicket(Mockito.eq("ABCD"))).thenReturn(Optional.of(new Ticket()));
    	when(ticketDAO.getExistingParkedVehicleTicket(Mockito.eq("EFGH"))).thenReturn(Optional.empty());
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("EFGH");
    	//when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCD").thenReturn("EFGH");
    	
    	
    	String result = parkingService.searchExistingParkedVehicle("ABCD");
        verify(ticketDAO, Mockito.times(2)).getExistingParkedVehicleTicket(anyString());
        assertEquals("EFGH",result);
    }
    
    

   /*
    @Test
    public void discountForRegularCustomerTest() {
    	fareCalculatorService = new FareCalculatorService();
    	
    	when(ticketDAO.getPreviousRegistration(anyString())).thenReturn(Optional.of(new Ticket()));
    	parkingService.processExitingVehicle();
    	
        verify(fareCalculatorService, Mockito.times(1)).calculateFareForRegularCustomer(any(Ticket.class));
    }
    */
     
 
    @Test
    public void processExitingVehicleTest() throws Exception{
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    
   
    
    
    
    
}
