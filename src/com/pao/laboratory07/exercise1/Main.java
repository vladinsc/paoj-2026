package com.pao.laboratory07.exercise1;

import com.pao.laboratory07.exercise1.exceptions.CannotCancelFinalOrderException;
import com.pao.laboratory07.exercise1.exceptions.CannotRevertInitialOrderStateException;
import com.pao.laboratory07.exercise1.exceptions.OrderIsAlreadyFinalException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        OrderState initialState = OrderState.valueOf(scanner.next());
        Order order = new Order(initialState);
        // Testele asteapta formatul asta in limba engleza
        System.out.println("Initial order state: " + initialState);

        while (scanner.hasNext()) {
            String cmdString = scanner.next();
            OrderCommand orderCommand = OrderCommand.valueOf(cmdString);

            switch (orderCommand) {
                case next -> {
                    try {
                        order.nextState();
                    } catch (OrderIsAlreadyFinalException e) {
                        System.out.println("Order is already in a final state.");
                    }
                }
                case cancel -> {
                    try {
                        order.cancel();
                    } catch (CannotCancelFinalOrderException e) {
                        System.out.println("Cannot cancel a final state order.");
                    }
                }
                case undo -> {
                    try {
                        order.undoState();
                    } catch (CannotRevertInitialOrderStateException e) {
                        System.out.println("Cannot undo the initial order state.");
                    }
                }
                case QUIT -> {
                    System.out.println("User quit the program.");
                    return;
                }
            }
        }
    }
}