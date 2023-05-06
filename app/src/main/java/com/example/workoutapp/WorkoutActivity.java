package com.example.workoutapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkoutActivity extends AppCompatActivity {

    private TextView workout_name, minutes, seconds, separate, status;
    private pl.droidsonroids.gif.GifImageView gifView;
    private AppCompatButton start, next;
    private Spinner duration, rest;
    private List<Workout> workouts;
    private ArrayAdapter<CharSequence> durationAdapter, restAdapter;
    private int currentWorkout = 0;

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        workout_name = findViewById(R.id.workout_name);
        start = findViewById(R.id.start_button);
        next = findViewById(R.id.next_button);
        duration = findViewById(R.id.duration);
        rest = findViewById(R.id.rest);
        gifView = findViewById(R.id.gif_name);
        minutes = findViewById(R.id.min_text);
        seconds = findViewById(R.id.second_text);
        separate = findViewById(R.id.separate_text);
        status = findViewById(R.id.status_text);

        workouts = WorkoutStore.getWorkouts();

        gifView.setImageResource(getGif(currentWorkout));
        workout_name.setText(workouts.get(currentWorkout).getWorkout());

        next.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);

        durationAdapter = ArrayAdapter.createFromResource(this, R.array.durations, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duration.setAdapter(durationAdapter);

        int selectedDuration = durationAdapter.getPosition(workouts.get(currentWorkout).getDuration().toString());
        duration.setSelection(selectedDuration);

        restAdapter = ArrayAdapter.createFromResource(this, R.array.durations, android.R.layout.simple_spinner_item);
        restAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rest.setAdapter(restAdapter);

        int selectedRest = durationAdapter.getPosition(workouts.get(currentWorkout).getRest().toString());
        rest.setSelection(selectedRest);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent notificationRecIntent = new Intent(this, NotificationReceiver.class);
        notificationRecIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPIntent = PendingIntent.getActivity(this, 0, notificationRecIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myCh")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Vibe Notification")
                .setContentText("Current workout is finished")
                .addAction(R.mipmap.ic_launcher, "Next Workout", notificationPIntent);


        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(this);

        duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                workouts.get(currentWorkout).setDuration(duration.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                workouts.get(currentWorkout).setRest(rest.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentWorkout < workouts.size() - 1) {
                    start.setVisibility(View.VISIBLE);
                    next.setVisibility(View.INVISIBLE);
                    currentWorkout++;

                    if (currentWorkout == 4) next.setText("Finish");

                    status.setText("Workout");

                    gifView.setImageResource(getGif(currentWorkout));
                    workout_name.setText(workouts.get(currentWorkout).getWorkout());

                    int selectedDuration = durationAdapter.getPosition(workouts.get(currentWorkout).getDuration().toString());
                    duration.setSelection(selectedDuration);

                    int selectedRest = durationAdapter.getPosition(workouts.get(currentWorkout).getRest().toString());
                    rest.setSelection(selectedRest);
                } else {
                    Intent mainIntent = new Intent(WorkoutActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] timeArray = workouts.get(currentWorkout).getDuration().toString().split(":");

                int totalTimeInSeconds = Integer.parseInt(timeArray[0]) * 60 + Integer.parseInt(timeArray[1]);

                new CountDownTimer(totalTimeInSeconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                                String[] timeMinSec = time.split(":");

                                if (timeMinSec[1] == "00" && timeMinSec[2] == "10") {
                                    minutes.setTextColor(Color.RED);
                                    seconds.setTextColor(Color.RED);
                                }

                                minutes.setText(timeMinSec[1]);
                                seconds.setText(timeMinSec[2]);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        status.setText("Rest");

                        String[] restArray = workouts.get(currentWorkout).getRest().toString().split(":");
                        int totalRestInSeconds = Integer.parseInt(restArray[0]) * 60 + Integer.parseInt(restArray[1]);

                        new CountDownTimer(totalRestInSeconds * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinishedRest) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinishedRest),
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinishedRest) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinishedRest)),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinishedRest) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinishedRest)));

                                        String[] timeMinSec = time.split(":");

                                        if (timeMinSec[1] == "00" && timeMinSec[2] == "10") {
                                            minutes.setTextColor(Color.RED);
                                            seconds.setTextColor(Color.RED);
                                        }

                                        minutes.setText(timeMinSec[1]);
                                        seconds.setText(timeMinSec[2]);
                                    }
                                });
                            }

                            @Override
                            public void onFinish() {
                                start.setVisibility(View.INVISIBLE);
                                next.setVisibility(View.VISIBLE);

                                if (ActivityCompat.checkSelfPermission(WorkoutActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                notificationManagerCompat.notify(currentWorkout, notification);
                            }
                        }.start();
                    }
                }.start();
            }
        });
    }

    private int getGif(int position) {
        switch (position) {
            case 0:
                return R.drawable.workout1;
            case 1:
                return R.drawable.workout2;
            case 2:
                return R.drawable.workout3;
            case 3:
                return R.drawable.workout4;
            case 4:
                return R.drawable.workout5;
            default:
                return -1;
        }
    }
}