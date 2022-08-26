package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        TicketPaymentServiceImpl paymentService = new TicketPaymentServiceImpl();

        SeatReservationServiceImpl seatReservationService = new SeatReservationServiceImpl();

        if(!validateAccountId(accountId)) throw new InvalidPurchaseException();

        if(!validateNumberOfTickets(ticketTypeRequests)) throw new InvalidPurchaseException();

        if(!validateAdultsPresent(ticketTypeRequests)) throw new InvalidPurchaseException();

        paymentService.makePayment(accountId, totalAmountToPay(ticketTypeRequests));

        seatReservationService.reserveSeat(accountId, totalSeatsToReserve(ticketTypeRequests));
    }


    private HashMap<TicketTypeRequest.Type, Integer> generatePricing() {
        HashMap<TicketTypeRequest.Type, Integer> pricing = new HashMap<>();
        pricing.put(TicketTypeRequest.Type.ADULT, 20);
        pricing.put(TicketTypeRequest.Type.CHILD, 10);
        pricing.put(TicketTypeRequest.Type.INFANT,0);

        return pricing;
    }

    private boolean validateAccountId(Long accountId) {

        if(accountId<1) {
            return false;
        }
        return true;

    }

    private boolean validateNumberOfTickets(TicketTypeRequest... ticketTypeRequests) {
        int ticketCount =0;
        int MAX_TICKET_AMOUNT =20;

        for (int i = 0; i < ticketTypeRequests.length; i++) {
            ticketCount += ticketTypeRequests[i].getNoOfTickets();
        }

        if(ticketCount>MAX_TICKET_AMOUNT) {
            return false;
        }

        return true;
    }

    private boolean validateAdultsPresent(TicketTypeRequest... ticketTypeRequests){

        for (int i = 0; i < ticketTypeRequests.length ; i++) {
            if(ticketTypeRequests[i].getTicketType()== TicketTypeRequest.Type.ADULT) {
                return true;
            }
        }
        return false;
    }

    private int totalAmountToPay(TicketTypeRequest... ticketTypeRequests) {
        HashMap<TicketTypeRequest.Type, Integer> pricing = generatePricing();
        int amountToPay=0;

        for(int i =0; i< ticketTypeRequests.length; i++) {
            TicketTypeRequest request = ticketTypeRequests[i];
            int amountPerRequest = (pricing.get(request.getTicketType()) * request.getNoOfTickets());
            amountToPay+= amountPerRequest;
        }
        return amountToPay;
    }

    private int totalSeatsToReserve(TicketTypeRequest... ticketTypeRequests) {
        int numberOfSeats =0;
        for (int i = 0; i < ticketTypeRequests.length; i++) {
            int seatsPerRequest =0;
            if(ticketTypeRequests[i].getTicketType()!= TicketTypeRequest.Type.INFANT) {
                seatsPerRequest = ticketTypeRequests[i].getNoOfTickets();
            }
            numberOfSeats+= seatsPerRequest;
        }

        return numberOfSeats;
    }

}
