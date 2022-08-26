package uk.gov.dwp.uc.pairtest;

import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
public class TicketServiceImplTest {

    TicketServiceImpl ticketServiceImpl = new TicketServiceImpl();


    TicketTypeRequest adultRequest = new TicketTypeRequest(
            TicketTypeRequest.Type.ADULT, 5
    );

    TicketTypeRequest childRequest = new TicketTypeRequest(
            TicketTypeRequest.Type.CHILD, 5
    );

    TicketTypeRequest infantRequest = new TicketTypeRequest(
            TicketTypeRequest.Type.INFANT, 2
    );


    @Test
    public void accountNumberShouldBeGreaterThanZero() {
        //given
        long accountId = 0;
        //then
        assertThrows(InvalidPurchaseException.class, () -> ticketServiceImpl.purchaseTickets(accountId, adultRequest));
    }

    @Test
    public void numberOfTicketsShouldBeLessThanTwenty() {

        TicketTypeRequest adultRequestMoreTickets = new TicketTypeRequest(
                TicketTypeRequest.Type.ADULT, 15
        );

        long accountId = 10;

        assertThrows(InvalidPurchaseException.class, () ->
                ticketServiceImpl.purchaseTickets(accountId, adultRequestMoreTickets, childRequest, infantRequest));
    }

    @Test
    public void adultShouldBePresentInTicketRequests() {
        long accountId = 10;

        assertThrows(InvalidPurchaseException.class, () ->
                ticketServiceImpl.purchaseTickets(accountId, childRequest, infantRequest));
    }

    @Test
    public void totalAmountToPayShouldBeCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //5 adults, 5 Children, 2 infants should total 150 (infants tickets cost 0)
        int amountToPayExpected = 150;

        //Method that needs to be tested is private, so using reflection to test
        Method method = TicketServiceImpl.class.getDeclaredMethod("totalAmountToPay",TicketTypeRequest[].class);
        method.setAccessible(true);

        TicketTypeRequest[] requests = new TicketTypeRequest[]{adultRequest, childRequest, infantRequest};

        int amountToPayActual = (int) method.invoke(ticketServiceImpl, (Object) requests);

        assertEquals(amountToPayExpected, amountToPayActual);
    }


    @Test
    public void totalSeatsToReseverShouldBeCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //5 adults, 5 children, 2 infants should total 10 seats (infants do not get seats)
        int numberOfSeatsExpected = 10;

        //Method that needs to best tested is private so using reflection to test

        Method method = TicketServiceImpl.class.getDeclaredMethod("totalSeatsToReserve", TicketTypeRequest[].class);
        method.setAccessible(true);

        TicketTypeRequest[] requests = new TicketTypeRequest[]{adultRequest, childRequest, infantRequest};

        int numberOfSeatsActual = (int) method.invoke(ticketServiceImpl, (Object) requests);

        assertEquals(numberOfSeatsExpected, numberOfSeatsActual);

    }


}