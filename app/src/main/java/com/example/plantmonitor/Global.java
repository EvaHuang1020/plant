package com.example.plantmonitor;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.HashMap;

public class Global extends Application {
    private String loginname,loginpassword;

    public HashMap<BluetoothDevice, Boolean> devicemap = new HashMap();
    public HashMap<BluetoothGatt, BluetoothGattCharacteristic> CharacteristicHashMap = new HashMap<>();

    public void setloginname(String name){
        this.loginname = name;
    }
    public void setloginpassword(String password ){
        this.loginpassword = password;
    }

    //取得 變數值
    public String getloginname() {
        return loginname;
    }
    public String getloginpassword(){
        return loginpassword;
    }


}
