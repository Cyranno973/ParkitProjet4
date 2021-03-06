package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

//        int inHour = ticket.getInTime().getHours();
//        int outHour = ticket.getOutTime().getHours();

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();


        //TODO: Some tests are failing here. Need to check if this logic is correct
       // int duration = outHour - inHour;

        double duration = outHour - inHour;

        double durationInminute = duration/ 1000d/60d;

        duration = duration/1000d/60d/60d;

        if (durationInminute < 30) {
            ticket.setPrice(0);
        }else {
            switch (ticket.getParkingSpot().getParkingType()){
                case CAR: {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                }
                case BIKE: {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                }
                default: throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
    public void calculateFare(Ticket ticket, double discount){
        calculateFare(ticket);
        ticket.setPrice(ticket.getPrice()*(1-discount/100f));
    }
}