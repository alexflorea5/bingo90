package com.lindar.bingo.service;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BingoServiceTest {
    private static final int NUMBER_OF_TICKETS = 6;
    private static final int NUMBER_OF_COLUMNS = 9;
    private static final int NUMBER_OF_ROWS = 3;
    private static final int BLANK_ELEMENTS_PER_ROW = 4;
    private static final int FILLED_VALUES_PER_ROW = 5;
    BingoService bingoService;

    @Before
    public void start() {
        bingoService = new BingoService();
    }

    @Test
    public void generateFinalTicket() {
        List<int[][]> ticketList;
        ticketList = bingoService.generateTicket();
        bingoService.prepareTicket(ticketList);

        //check if 6 tickets are generated
        assertEquals(NUMBER_OF_TICKETS, ticketList.size());
        // verify all 90 values are presents in the stripe
        Set<Integer> allBingoNumbers = IntStream.rangeClosed(1, 90)
                .boxed().collect(Collectors.toSet());
        assertEquals(90, allBingoNumbers.size());

        for (int i = 0; i < NUMBER_OF_TICKETS; ++i) {

            assertEquals(NUMBER_OF_ROWS, ticketList.get(i).length); //verify row number
            assertEquals(NUMBER_OF_COLUMNS, ticketList.get(i)[0].length); //verify column number
            //5 filled values an 4 empty for each row
            verifyFilledFieldsPerRow(ticketList.get(i));
            //at least 1 populated value per column
            verifyFilledFieldsPerColumn(ticketList.get(i));
            verifyColumnOrder(ticketList.get(i));

            removeFromNumberWhenIsFound(ticketList.get(i), allBingoNumbers);
        }
        assertEquals(0, allBingoNumbers.size());
    }

    private void removeFromNumberWhenIsFound(int[][] ticket, Set<Integer> allBingoNumbers) {
        for (int j = 0; j < NUMBER_OF_COLUMNS; ++j) {
            for (int i = 0; i < ticket.length; ++i) {
                if (ticket[i][j] != 0) {
                    allBingoNumbers.remove(ticket[i][j]);
                }
            }
        }
    }

    private void verifyFilledFieldsPerRow(int[][] ticket) {
        for (int i = 0; i < ticket.length; ++i) {
            long filledNumber = Arrays.stream(ticket[i]).filter(v -> v != 0).count();
            assertEquals(FILLED_VALUES_PER_ROW, filledNumber);
            assertEquals(BLANK_ELEMENTS_PER_ROW, NUMBER_OF_COLUMNS - FILLED_VALUES_PER_ROW);
        }
    }

    private void verifyFilledFieldsPerColumn(int[][] ticket) {
        for (int j = 0; j < NUMBER_OF_COLUMNS; ++j) {
            int filledPerColumn = 0;
            for (int i = 0; i < ticket.length; ++i) {
                if (ticket[i][j] != 0) {
                    verifyElementIsOnTheRightColumn(ticket[i][j], j);
                    filledPerColumn++;
                }
            }
            //at least one value should be filled on column
            assertTrue(filledPerColumn > 0);
        }
    }

    private void verifyElementIsOnTheRightColumn(int value, int column) {
        if (column == 0) {
            assertTrue(1 <= value && value <= 9);
        } else if (column < 8) {
            assertTrue(column * 10 <= value && value <= column * 10 + 9);
        } else {
            assertTrue(80 <= value && value <= 90);
        }
    }

    private void verifyColumnOrder(int[][] ticket) {
        for (int j = 0; j < NUMBER_OF_COLUMNS; ++j) {
            if (ticket[0][j] > ticket[1][j] && ticket[0][j] != 0 && ticket[1][j] != 0)
                fail();
            if (ticket[0][j] > ticket[2][j] && ticket[0][j] != 0 && ticket[2][j] != 0)
                fail();
            if (ticket[1][j] > ticket[2][j] && ticket[1][j] != 0 && ticket[2][j] != 0)
                fail();
        }
    }
}