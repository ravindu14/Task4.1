package com.example.workoutapp;

import java.util.ArrayList;
import java.util.List;

public class WorkoutStore {
    private static List<Workout> workouts() {

        final List<Workout> Workouts = new ArrayList<>();

        Workout workout1 = new Workout("Bicycle Crunches", "1:00", "0:30");
        Workout workout2 = new Workout("Chest Ups", "1:30", "0:30");
        Workout workout3 = new Workout("Push Ups", "2:00", "0:30");
        Workout workout4 = new Workout("Play Lunges", "1:30", "0:30");
        Workout workout5 = new Workout("Lunge Jumps", "1:00", "0:30");

        Workouts.add(workout1);
        Workouts.add(workout2);
        Workouts.add(workout3);
        Workouts.add(workout4);
        Workouts.add(workout5);

        return Workouts;
    }

    public static List<Workout> getWorkouts() {
        return workouts();
    }
}
