package com.wordpress.commonplayground.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wordpress.commonplayground.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddSessionActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPublish;
    private ImageButton btnDatePicker, btnTimePicker;
    private TextInputLayout title, game, place, date, time, numberOfPlayers, description;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        setUserID();
        setOnclickListeners();
        accessUIInputFields();
    }

    private void setUserID() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
        }
    }

    private void setOnclickListeners() {
        btnPublish = (Button) findViewById(R.id.ButtonPublish);
        btnPublish.setOnClickListener(this);

        btnDatePicker = (ImageButton) findViewById(R.id.btn_date);
        btnDatePicker.setOnClickListener(this);

        btnTimePicker = (ImageButton) findViewById(R.id.btn_time);
        btnTimePicker.setOnClickListener(this);
    }

    private void accessUIInputFields() {
        title = (TextInputLayout) findViewById(R.id.TitleInput);
        game = (TextInputLayout) findViewById(R.id.GameInput);
        place = (TextInputLayout) findViewById(R.id.PlaceInput);
        date = (TextInputLayout) findViewById(R.id.DateInput);
        time = (TextInputLayout) findViewById(R.id.TimeInput);
        numberOfPlayers = (TextInputLayout) findViewById(R.id.PlayersInput);
        description = (TextInputLayout) findViewById(R.id.DescriptionInput);
    }

    @Override
    public void onBackPressed() {
        Intent openMain = new Intent();
        openMain.putExtra("userID", userID);
        setResult(RESULT_OK, openMain);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == btnPublish) {
            sendRequestToBackend(view);
            returnToMainActivity();
        }

        if (view == btnDatePicker) {
            getCurrentDate();
        }

        if (view == btnTimePicker) {
            getCurrentTime();
        }

    }

    private void returnToMainActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        }, 100);
    }

    public void getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        launchDatePickerDialog();
    }

    private void launchDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date.getEditText().setText(((dayOfMonth >= 10) ? "" : "0") + dayOfMonth + "-" + ((monthOfYear + 1 >= 10) ? "" : "0") + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getCurrentTime() {
        final Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        launchTimePickerDialog();
    }

    private void launchTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.getEditText().setText(((hourOfDay >= 10) ? "" : "0") + hourOfDay + ":" + ((minute >= 10) ? "" : "0") + minute);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public void sendRequestToBackend(View view) {
        final String sessionTitle = title.getEditText().getText().toString();
        final String sessionGame = game.getEditText().getText().toString();
        final String sessionPlace = place.getEditText().getText().toString();
        final String sessionDate = date.getEditText().getText().toString();
        final String sessionTime = time.getEditText().getText().toString();
        final String sessionPlayers = numberOfPlayers.getEditText().getText().toString();
        final String sessionDesc = description.getEditText().getText().toString();

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:8080/postNewSession";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());
                Snackbar.make(view, getString(R.string.new_response_fine), 5000)
                        .setAction("Action", null).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", String.valueOf(error));
                Snackbar.make(view, getString(R.string.new_error), 5000)
                        .setAction("Action", null).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("title", sessionTitle);
                MyData.put("description", sessionDesc);
                MyData.put("game", sessionGame);
                MyData.put("place", sessionPlace);
                MyData.put("date", sessionDate);
                MyData.put("time", sessionTime);
                MyData.put("numberOfPlayers", sessionPlayers);
                MyData.put("idOfHost", userID);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}