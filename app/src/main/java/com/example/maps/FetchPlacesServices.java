package com.example.maps;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

public class FetchPlacesServices extends IntentService {
    public static String NOTIFICATION = "udb.edu.sv.dasguia03";
    public static String RESULT = "dataResult";
    private ArrayList<Place> result = new ArrayList();
    public FetchPlacesServices(String name) {
        super("fetchplaces");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        result.add(new Place("Spain Square Sport Complex",13.7154415,-89.1458651));
        result.add(new Place("Complejo Deportivo Montes de San Bartolo III",13.7154415,-89.1458651));
        result.add(new Place("Complejo Educativo Montes de San Bartolo IV",13.7188646,-89.1469404));
        publishData();
    }

    public void publishData(){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
