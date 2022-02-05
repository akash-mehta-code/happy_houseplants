package com.akashcode.happyhouseplants.dal;

import java.util.Collections;
import java.util.List;

public class PlantHelper {
    public static void sortPlants(List<Plant> plantList, SortType sortType) {
        switch (sortType) {
            case PLANT_NAME:
                Collections.sort(plantList, new Plant.SortByName());
                break;
            case WATER_LEVEL:
                Collections.sort(plantList, new Plant.SortByWaterLevel());
                break;
            case DAYS_UNTIL_NEXT_WATERING:
                Collections.sort(plantList, new Plant.SortByDaysUntilNextWatering());
                break;
            default:
                return;
        }
    }

    public enum SortType {
        PLANT_NAME,
        DAYS_UNTIL_NEXT_WATERING,
        WATER_LEVEL
    }
}
