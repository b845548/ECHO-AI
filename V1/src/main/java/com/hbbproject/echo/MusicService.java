package com.hbbproject.echo;
import android.net.Uri;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import java.util.Random;
import android.os.Environment;
import java.io.File; 

public class MusicService extends Service {  
  public MediaPlayer mp;
  public IBinder onBind(Intent arg0) {
    return null;
  }

 public void onStart(Intent intent, int startId) {    
  super.onStart(intent, startId);
  File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
  String str[]= f.list();
  int len=str.length;
  Random rand = new Random();

  int rdnb = rand.nextInt(len);
    try{
       mp = MediaPlayer.create(this, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/"+str[rdnb]));
      mp.start();
    }catch(Exception e){}
    
  }

 public void onDestroy() {
    super.onDestroy();
    mp.stop();
    //mp.release();
    }
 }
