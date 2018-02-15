package com.example.dj.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class visualizeFrag extends Fragment implements View.OnClickListener{
    Button temperature, humidity,peepBtn,oxBtn,redBtn,codBtn,luminBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View windows = inflater.inflate(R.layout.fragment_visualize, container, false);
        temperature = (Button)windows.findViewById(R.id.temp);
        humidity = (Button) windows.findViewById(R.id.humid);
        luminBtn=(Button) windows.findViewById(R.id.lumin);
        peepBtn = (Button) windows.findViewById(R.id.peepbtn);
        oxBtn= (Button) windows.findViewById(R.id.oxbtn);
        redBtn = (Button) windows.findViewById(R.id.redbtn);
        codBtn = (Button) windows.findViewById(R.id.codbtn);

        Typeface f = Typeface.createFromAsset(getActivity().getAssets(), "marker.TTF");
        temperature.setTypeface(f);
        peepBtn.setTypeface(f);
        oxBtn.setTypeface(f);
        redBtn.setTypeface(f);
        codBtn.setTypeface(f);
        humidity.setTypeface(f);
        luminBtn.setTypeface(f);

        temperature.setOnClickListener(this);
        humidity.setOnClickListener(this);
        luminBtn.setOnClickListener(this);
        peepBtn.setOnClickListener(this);
        oxBtn.setOnClickListener(this);
        redBtn.setOnClickListener(this);
        codBtn.setOnClickListener(this);
        return windows;
    }
    public void tempClicked(){
        Toast.makeText(getActivity(),"Temperature Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),Temperature.class);
        getActivity().startActivity(intent);
    }
    public void humidClicked(){
        Toast.makeText(getActivity(),"Humidity Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),Humidity.class);
        getActivity().startActivity(intent);
    }
    public void LuminClicked(){
        Toast.makeText(getActivity(),"Luminosity Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),Luminosity.class);
        getActivity().startActivity(intent);
    }
    public void PeepCountClicked(){
        Toast.makeText(getActivity(),"People Count Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),PeopleCount.class);
        getActivity().startActivity(intent);
    }
    public void OxClicked(){
        Toast.makeText(getActivity(),"Oxidising Gas Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),OxidizingGas.class);
        getActivity().startActivity(intent);
    }
    public void RedClicked(){
        Toast.makeText(getActivity(),"Reducing Gas Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),RedGas.class);
        getActivity().startActivity(intent);
    }
    public void CODClicked(){
        Toast.makeText(getActivity(),"Carbon Dioxide Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(),CarbonDioxide.class);
        getActivity().startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.temp:
                tempClicked();
                break;
            case R.id.humid:
                humidClicked();
                break;
            case  R.id.lumin:
                LuminClicked();
                break;
            case  R.id.peepbtn:
                PeepCountClicked();
                break;
            case  R.id.oxbtn:
                OxClicked();
                break;
            case  R.id.redbtn:
                RedClicked();
                break;
            case  R.id.codbtn:
                CODClicked();
                break;
        }
    }

}
