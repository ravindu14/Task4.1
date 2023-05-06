package com.example.workoutapp;

public class Workout {
    private String workout, duration, rest;

    public Workout(String workout, String duration, String rest) {
        this.workout = workout;
        this.duration = duration;
        this.rest = rest;
    }

    public String getWorkout() {
        return workout;
    }

    public String getDuration() {
        return duration;
    }

    public String getRest() {
        return rest;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }
}
