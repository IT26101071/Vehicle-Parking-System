package com.parking.dsa;

import com.parking.model.ParkingSlot;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ParkingSlotStack {

    private final Deque<ParkingSlot> stack = new ArrayDeque<>();

    public void loadAvailableSlots(List<ParkingSlot> slots) {
        stack.clear();

        for (ParkingSlot slot : slots) {
            if ("AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
                stack.push(slot);
            }
        }
    }


    public ParkingSlot pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException(
                    "No available parking slots"
            );
        }

        return stack.pop();
    }

    public ParkingSlot peek() {
        if (stack.isEmpty()) {
            throw new IllegalStateException(
                    "No available parking slots"
            );
        }

        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}