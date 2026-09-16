package com.example.wififieldlab;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    WifiManager wm; Handler h=new Handler(); TextView dbm,network,stats,chart; EditText material;
    ArrayList<Integer> values=new ArrayList<>(); int min=0,max=0; double sum=0;
    Runnable loop=new Runnable(){public void run(){measure();h.postDelayed(this,1000);}};
    public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_main);
      dbm=findViewById(R.id.dbm);network=findViewById(R.id.network);stats=findViewById(R.id.stats);
      chart=findViewById(R.id.chart);material=findViewById(R.id.material);
      wm=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
      if(android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},7);
      findViewById(R.id.refresh).setOnClickListener(v->measure());
      findViewById(R.id.record).setOnClickListener(v->record());
      h.post(loop);
    }
    void measure(){try{
      WifiInfo i=wm.getConnectionInfo(); int d=i.getRssi(); if(d>-1){values.add(d); if(values.size()>60)values.remove(0);
      min=Collections.min(values);max=Collections.max(values);sum=0;for(int x:values)sum+=x;
      dbm.setText(d+" dBm"); network.setText("Rede: "+String.valueOf(i.getSSID()).replace(""",""));
      stats.setText(String.format(Locale.US,"Média: %.1f   Mín: %d   Máx: %d",sum/values.size(),min,max)); draw();}
    }catch(Exception e){dbm.setText("Sem sinal");}}
    void draw(){StringBuilder s=new StringBuilder(); for(int x:values){int n=Math.max(1,Math.min(24,(x+100)/3));s.append(String.format("%4d |",x));for(int j=0;j<n;j++)s.append("█");s.append("\n");}chart.setText(s.toString());}
    void record(){String m=material.getText().toString().trim();if(m.isEmpty())m="sem etiqueta";Toast.makeText(this,"Registrado: "+m+" — "+dbm.getText(),Toast.LENGTH_SHORT).show();}
    protected void onDestroy(){h.removeCallbacks(loop);super.onDestroy();}
}
