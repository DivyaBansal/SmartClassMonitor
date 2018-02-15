package com.example.dj.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Chart extends AppCompatActivity {
    Button temperature, humidity,peepBtn,oxBtn,redBtn,codBtn,luminBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chart);
        temperature = (Button) findViewById(R.id.temp);
        humidity = (Button) findViewById(R.id.humid);
        luminBtn=(Button) findViewById(R.id.lumin);
        peepBtn = (Button) findViewById(R.id.peepbtn);
        oxBtn= (Button) findViewById(R.id.oxbtn);
        redBtn = (Button) findViewById(R.id.redbtn);
        codBtn = (Button) findViewById(R.id.codbtn);

    }
    public void tempClicked(View view){
        Toast.makeText(getBaseContext(),"Temperature Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,Temperature.class);
        startActivity(intent);
    }
    public void humidClicked(View view){
        Toast.makeText(getBaseContext(),"Humidity Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,Humidity.class);
        startActivity(intent);
    }
    public void LuminClicked(View view){
        Toast.makeText(getBaseContext(),"Luminosity Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,Luminosity.class);
        startActivity(intent);
    }
    public void PeepCountClicked(View view){
        Toast.makeText(getBaseContext(),"People Count Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PeopleCount.class);
        startActivity(intent);
    }
    public void OxClicked(View view){
        Toast.makeText(getBaseContext(),"Oxidising Gas Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,OxidizingGas.class);
        startActivity(intent);
    }
    public void RedClicked(View view){
        Toast.makeText(getBaseContext(),"Reducing Gas Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,RedGas.class);
        startActivity(intent);
    }
    public void CODClicked(View view){
        Toast.makeText(getBaseContext(),"Carbon Dioxide Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,CarbonDioxide.class);
        startActivity(intent);
    }

}
