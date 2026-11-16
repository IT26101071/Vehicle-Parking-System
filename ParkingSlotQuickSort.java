package com.parking.dsa;

import com.parking.model.ParkingSlot;

import java.util.ArrayList;
import java.util.List;

public class ParkingSlotQuickSort {

    public static List<ParkingSlot> sortByAvailability(
            List<ParkingSlot> slots) {

        List<ParkingSlot> sorted = new ArrayList<>(slots);

        if (sorted.size() > 1) {
            quickSort(sorted, 0, sorted.size() - 1);
        }

        return sorted;
    }

    private static void quickSort(
            List<ParkingSlot> slots, int low, int high) {

        if (low < high) {
            int pivotIndex = partition(slots, low, high);

            quickSort(slots, low, pivotIndex - 1);
            quickSort(slots, pivotIndex + 1, high);
        }
    }

    private static int partition(
            List<ParkingSlot> slots, int low, int high) {

        ParkingSlot pivot = slots.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {

            if (compare(slots.get(j), pivot) <= 0) {
                i++;
                swap(slots, i, j);
            }
        }

        swap(slots, i + 1, high);

        return i + 1;
    }

    private static int compare(ParkingSlot a, ParkingSlot b) {

        int statusCompare = Integer.compare(
                getPriority(a.getStatus()),
                getPriority(b.getStatus())
        );

        if (statusCompare != 0) {
            return statusCompare;
        }

        return a.getSlotNumber()
                .compareToIgnoreCase(b.getSlotNumber());
    }

    private static int getPriority(String status) {

        if (status == null) return 5;

        return switch (status.toUpperCase()) {
            case "AVAILABLE" -> 1;
            case "RESERVED" -> 2;
            case "OCCUPIED" -> 3;
            case "MAINTENANCE" -> 4;
            default -> 5;
        };
    }

    private static void swap(
            List<ParkingSlot> slots, int i, int j) {

        ParkingSlot temp = slots.get(i);
        slots.set(i, slots.get(j));
        slots.set(j, temp);
    }
}