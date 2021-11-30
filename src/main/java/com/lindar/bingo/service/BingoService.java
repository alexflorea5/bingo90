package com.lindar.bingo.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lindar.bingo.common.Constants.*;

public class BingoService {

    /**
     * After this step, we will have 6 tickets with 15 unique values
     * row one will be fully populated with 9 values and row 2/3  will have 6 elements distributed randomly
     */
    public List<int[][]> generateTicket() {
        List<int[][]> ticketList = IntStream.range(0, NUMBER_OF_TICKETS)
                .mapToObj(x -> new int[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS])
                .collect(Collectors.toList());
        //keep columns which can be populated (ex: if all elements from column 5 were used in tickets, then remove 5 from usableValues)
        List<Integer> usableValues = IntStream.range(0, NUMBER_OF_COLUMNS).boxed()
                .collect(Collectors.toList());
        List<LinkedList<Integer>> columnNumberLists = generateRanges();

        /**
         * Get the mandatory 9 values for ticket (one for each column)
         * for a ticket every column should have at least one element ==> we pick one element from each range
         */
        for (int i = 0; i < NUMBER_OF_TICKETS; i++) {
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                ticketList.get(i)[0][j] = columnNumberLists.get(j).peek();
                columnNumberLists.get(j).remove();//element will be removed after is picked (can't be used again for other ticket)
            }
        }

        /**
         * Get the next 6 values for every ticket
         * after previous step all 6 tickets will have 9 values from each range
         * we need 15 values for each ticket ==> we need 6 more elements
         */
        for (int i = 0; i < NUMBER_OF_TICKETS; i++) {
            List<Integer> positions = new ArrayList<>();
            if (i < FILLED_VALUES_PER_ROW) {
                positions = getColumnPositions(columnNumberLists, usableValues);
            } else {
                //before getting positions for the last ticket, check if values were distributed ok
                //for example if for 70-79 range we have remaining 3 values is not ok because in last ticket we does not have enough
                //space on the column, if is not ok, generate all ticket again
                //TODO find a way to avoid this situation
                boolean redo = columnNumberLists.stream().anyMatch(e -> e.size() > 2);
                if (!redo) {
                    positions = getColumnPositions(columnNumberLists, usableValues);
                } else {
                    ticketList = generateTicket();
                }
            }

            for (int pos : positions) {
                //add new element on row 1 if is not populated
                if (ticketList.get(i)[1][pos] == 0) {
                    ticketList.get(i)[1][pos] = columnNumberLists.get(pos).peek();
                } else {
                    ticketList.get(i)[2][pos] = columnNumberLists.get(pos).peek();
                }
                columnNumberLists.get(pos).remove();

                if (columnNumberLists.get(pos).isEmpty()) {
                    usableValues.removeIf(value -> value == pos);
                }
            }
        }

        return ticketList;
    }

    /**
     * Get the next 6 positions (columns to be populated)
     */
    private List<Integer> getColumnPositions(List<LinkedList<Integer>> columnNumberLists, List<Integer> usableValues) {
        //this map will contain a column number as a key (0–8 interval) and a number that can be 1 or 2
        //and represent the number of times we will populate the column from the key.
        Map<Integer, Integer> columnsMap = new HashMap<>();
        for (int i = 1; i <= NUMBER_OF_TICKETS; ++i) {
            getRandomNumbersInRange(columnsMap, columnNumberLists, usableValues);
        }
        List<Integer> columns = new ArrayList();

        //put in a list all columns
        for (int column : columnsMap.keySet()) {
            int nr = columnsMap.get(column);
            while (nr > 0) {
                columns.add(column);
                nr--;
            }
        }

        return columns;
    }

    /**
     * this method will generate a random positions (column)
     * value will be in map (key=column, value=nr <=2)
     */
    private void getRandomNumbersInRange(Map<Integer, Integer> columnsMap, List<LinkedList<Integer>> columnNumberLists, List<Integer> usableValues) {
        List<Integer> list = betterDistributionForColumns(columnNumberLists);
        int randPoz = random.nextInt(list.size());
        Integer rand = list.get(randPoz);

        //generate again if column is populate 2 times already (columnsMap.get(rand) + 1 > 2) or
        //range for that column does not have enough values available ( columnsMap.get(rand) + 1 > columnNumberLists.get(rand).size())
        while ((columnsMap.get(rand) != null && columnsMap.get(rand) + 1 > 2)
                || (columnsMap.get(rand) != null && columnsMap.get(rand) + 1 > columnNumberLists.get(rand).size())) {
            randPoz = random.nextInt(usableValues.size());
            rand = usableValues.get(randPoz);
        }

        //add value in map if not exists or increase value if is already there
        if (columnsMap.containsKey(rand)) {
            int k = columnsMap.get(rand);
            columnsMap.put(rand, k + 1);
        } else {
            columnsMap.put(rand, 1);
        }
    }

    /**
     * for better distribution of the elements
     */
    private List<Integer> betterDistributionForColumns(List<LinkedList<Integer>> columnNumberLists) {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < columnNumberLists.size(); ++i) {
            if (columnNumberLists.get(i).size() > 0) {
                for (int j = 1; j <= columnNumberLists.get(i).size(); ++j)
                    list.add(i);
            }
        }
        return list;
    }

    /**
     * Generate all 90 values in different lists coresponding the ranges
     * there will be a list which contains 9 linked lists
     * each linked list will have elements from a range
     * ex: 1.........9
     * 10.......19
     * ...........
     * 80.......90
     * those elements will be shuffeled in each linked list
     */
    private List<LinkedList<Integer>> generateRanges() {

        List<LinkedList<Integer>> columnNumberLists = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (i == 0) {
                LinkedList integers = getLinkedList(1, NUMBER_OF_COLUMNS);
                columnNumberLists.add(integers);
            } else if (i < 8) {
                LinkedList integers = getLinkedList(i * 10, i * 10 + 10 - 1);
                columnNumberLists.add(integers);
            } else {
                LinkedList integers = getLinkedList(80, 90);
                columnNumberLists.add(integers);
            }
        }

        return columnNumberLists;
    }

    /**
     * Generate numbers in a range, add to a linked list and shuffle the list
     * ex: for start=1 and stop=9
     * we can have 7 1 9 3 4 6 2 5 8
     */
    private LinkedList getLinkedList(int start, int stop) {
        LinkedList integers = IntStream.rangeClosed(start, stop).boxed().collect(Collectors.toCollection(LinkedList::new));
        Collections.shuffle(integers);
        return integers;
    }

    /**
     * This method will distribute elements on the ticket
     * 5 for each row
     */
    public void prepareTicket(List<int[][]> ticketList) {
        for (int i = 0; i < NUMBER_OF_TICKETS; i++) {
            List<Integer> move = pickRandomElementsFromFirstRow(ticketList.get(i), BLANK_ELEMENTS_PER_ROW);
            for (int k = 0; k < BLANK_ELEMENTS_PER_ROW; ++k) {
                putValueRandomOnRowTwoOrThree(ticketList, i, move, k);
            }
            echilibrateLastTwoRow(ticketList, i);
            sortColumns(ticketList.get(i));
        }
    }

    private void echilibrateLastTwoRow(List<int[][]> ticketList, int i) {
        //now we know row 1 has 5 elements and
        int nonEmptyElementsFromRow = countNonEmptyElementsFromRow(ticketList.get(i), 1);
        List<Integer> change = new ArrayList<>();
        int requiredChanges = 0;
        int row = 0;
        int rowToBeChanged = 0;
        if (nonEmptyElementsFromRow > FILLED_VALUES_PER_ROW) {
            //move rest of the elements from row 2 to row 3
            requiredChanges = nonEmptyElementsFromRow - FILLED_VALUES_PER_ROW;
            row = 1;
            rowToBeChanged = 2;
            change = pickRandomElementsFromGivenRow(ticketList.get(i), row, rowToBeChanged, requiredChanges);
        } else if (nonEmptyElementsFromRow < FILLED_VALUES_PER_ROW) {
            //move rest of the elements from row 3 to row 2
            requiredChanges = FILLED_VALUES_PER_ROW - nonEmptyElementsFromRow;
            row = 2;
            rowToBeChanged = 1;
            change = pickRandomElementsFromGivenRow(ticketList.get(i), row, rowToBeChanged, requiredChanges);
        }

        for (int k = 0; k < requiredChanges; ++k) {
            ticketList.get(i)[rowToBeChanged][change.get(k)] = ticketList.get(i)[row][change.get(k)];
            ticketList.get(i)[row][change.get(k)] = 0;
        }
    }

    private void putValueRandomOnRowTwoOrThree(List<int[][]> ticketList, int i, List<Integer> move, int k) {
        boolean row = random.nextBoolean();
        if (row) {
            if (ticketList.get(i)[1][move.get(k)] == 0) {
                ticketList.get(i)[1][move.get(k)] = ticketList.get(i)[0][move.get(k)];
            } else {
                ticketList.get(i)[2][move.get(k)] = ticketList.get(i)[0][move.get(k)];
            }
        } else {
            if (ticketList.get(i)[2][move.get(k)] == 0) {
                ticketList.get(i)[2][move.get(k)] = ticketList.get(i)[0][move.get(k)];
            } else {
                ticketList.get(i)[1][move.get(k)] = ticketList.get(i)[0][move.get(k)];
            }
        }
        ticketList.get(i)[0][move.get(k)] = 0;
    }

    private void sortColumns(int[][] ticket) {
        for (int k = 0; k < NUMBER_OF_COLUMNS; ++k) {
            for (int i = 0; i < NUMBER_OF_ROWS; ++i) {
                for (int j = i + 1; j < NUMBER_OF_ROWS; ++j) {
                    if (ticket[i][k] != 0 && ticket[j][k] != 0 &&
                            ticket[i][k] > ticket[j][k]) {
                        int aux = ticket[i][k];
                        ticket[i][k] = ticket[j][k];
                        ticket[j][k] = aux;
                    }
                }
            }
        }
    }

    private int countNonEmptyElementsFromRow(int[][] ticket, int row) {
        int nonEmptyElements = 0;
        for (int i = 0; i < NUMBER_OF_COLUMNS; ++i) {
            if (ticket[row][i] != 0) {
                nonEmptyElements++;
            }
        }
        return nonEmptyElements;
    }

    private List<Integer> pickRandomElementsFromFirstRow(int[][] ticket, int numberOfElements) {
        List<Integer> foundValues = new ArrayList<>();
        for (int i = 1; i <= numberOfElements; ++i) {
            positionsToChange(ticket, foundValues);
        }
        return foundValues;
    }

    private void positionsToChange(int[][] ticket, List<Integer> foundValues) {
        Integer position = random.nextInt(NUMBER_OF_COLUMNS);
        while (foundValues.contains(position) || (ticket[1][position] != 0 && ticket[2][position] != 0)) {
            position = random.nextInt(NUMBER_OF_COLUMNS);
        }
        foundValues.add(position);
    }

    private List<Integer> pickRandomElementsFromGivenRow(int[][] ticket, int row, int rowToBeChanged,
                                                         int numberOfElements) {
        List<Integer> foundValues = new ArrayList<>();
        for (int i = 0; i < numberOfElements; ++i) {
            positionsToChange(ticket, foundValues, row, rowToBeChanged);
        }
        return foundValues;
    }

    private void positionsToChange(int[][] ticket, List<Integer> foundValues, int row, int rowToBeChanged) {
        Integer position = random.nextInt(NUMBER_OF_COLUMNS);

        while (foundValues.contains(position) || ticket[row][position] == 0 || ticket[rowToBeChanged][position] != 0) {
            position = random.nextInt(NUMBER_OF_COLUMNS);
        }
        foundValues.add(position);
    }
}
