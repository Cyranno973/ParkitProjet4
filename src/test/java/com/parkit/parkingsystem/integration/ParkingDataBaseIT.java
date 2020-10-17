package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import junit.framework.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        int nextParkingSpot =  parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        parkingService.processIncomingVehicle();


        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability

        //e1 recuperer le ticket du vehicule imatriculer 'ABCDEF'
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //ETape2 verifier l'existence du ticket
        Assert.assertNotNull(ticket);

        //ETape3 recuperer le parking spot
        ParkingSpot parkingSpot = ticket.getParkingSpot();

        //ETape4 verifier son existence
        Assert.assertNotNull(parkingSpot);

        //ETape5 verfier l'etat de la colonne availaibale est a false
        Assertions.assertFalse(parkingSpot.isAvailable());

        //ETape5 verfier l'etat de la colonne availaibale est a false
        Assertions.assertFalse(parkingSpot.isAvailable());

        //ETape6 verfier que la prochaine place disponible a bien été utliser
        Assertions.assertEquals(nextParkingSpot,parkingSpot.getId());

    }

    @Test
    public void testParkingLotExit() throws InterruptedException {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        Thread.sleep(5000); // sinon l'entrer et la sortie est faite en quelque millieme de seconde
        parkingService.processExitingVehicle();

        //TODO: check that the fare generated and out time are populated correctly in the database

        //Etape on recupere le ticket de la voiture
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //Etape on verifie l'éxistence
        Assert.assertNotNull(ticket);

        //Etape on recupere le time in
         Date timeIn = ticket.getInTime();

        //Etape on verifie l'éxistence
        Assert.assertNotNull(timeIn);

        //Etape on recupere le time out
        Date timeOut = ticket.getOutTime();

        //Etape on verifie l'éxistence
        Assert.assertNotNull(timeOut);

        //Etape on recupere le prix du ticket pour une voiture
        double price = ticket.getPrice();
        System.out.println(price);

        // on verifie son existance
        Assert.assertNotNull(price);


        // Etape verfication du pris attendu et du prix obtenu
        // 5d corespond au 5 sec attendu( Thread.sleep(5000));
        Assertions.assertEquals(Math.round((5d/3600d)* Fare.CAR_RATE_PER_HOUR),Math.round(ticket.getPrice()));

    }

}
