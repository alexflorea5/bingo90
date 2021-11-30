package com.lindar.bingo;

import com.lindar.bingo.service.BingoService;

import java.util.*;
import java.util.stream.Stream;

import static com.lindar.bingo.common.Constants.*;

public class BingoGenerator {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long finish = System.currentTimeMillis();
        long timeElapsed;
        BingoService bingoService = new BingoService();

        buildTickets(bingoService, 1, true);

        finish = System.currentTimeMillis();
        timeElapsed = finish - start;

        System.out.println("Time in milliseconds: " + timeElapsed);
    }

    private static void buildTickets(BingoService bingoService, int number, boolean print) {
        if (number < 1)
            number = 1;
        List<int[][]> ticketList;
        for (int asd = 1; asd <= number; ++asd) {
            ticketList = bingoService.generateTicket();
            bingoService.prepareTicket(ticketList);

            if (print) {
                printStripe(ticketList);
            }
        }

    }

    private static void printStripe(List<int[][]> ticketList) {
        for (int ticket = 0; ticket < NUMBER_OF_TICKETS; ticket++) {
            Stream.of(ticketList.get(ticket)).map(Arrays::toString).forEach(System.out::println);
            System.out.println();
        }
    }
}
