package com.hbbproject.echoAI;

import android.text.TextWatcher;
import android.text.Editable;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor; 
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.speech.tts.TextToSpeech;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.net.Uri;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File; 
import java.net.Socket;
import java.util.Locale;
import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;

import com.hbbproject.echoAI.alice.Alice;


public class MainActivity extends Activity {
//////////////// Server TCP IP
    String serverIP ="192.168.1.156";
    String serverExternIP="http://hbbproject.pagekite.me/";
    int serverPort = 8000;
    int serverExternPort = 8000;
    //////////////// Server TCP IP

    boolean playingMusic=false; 
    boolean tutorial=false;

    //////////////// voice recognition
    SpeechRecognizer mRecognizer;
    Intent recognizerIntent;    
    //////////////// voice recognition

    //////////////// voice synthetizer
    TextToSpeech textToSpeech;
    //////////////// voice synthetizer
    TextView messageReceive;
    ViewPager mPager;
    int pageActuel=0;
    SharedPreferences sp;
    String USER_PREFS="SUASDXZ";
    SharedPreferences.Editor editor;
    TextView tv1;
    Context actuelContext;

    private static final String FRAGMENT_DIALOG_LOG_TAG = "BrainLoggerDialog";

  //  private ListView chatListView;
//    private static ChatArrayAdapter adapter;
    private EditText chatEditText;
    private BrainLoggerDialog dialog;
    private ResponseReceiver mMessageReceiver;
    EditText edt;
    
    int loading=0;
    int loaded=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
    String bot_name_path="alice"; 
    String aiml_path = bot_name_path+"/aiml";
    String sets_path = bot_name_path+"/sets";
    String maps_path = bot_name_path+"/maps";
    Context c=getApplicationContext();
    try{
        String[] listOfFiles = c.getAssets().list(aiml_path);
        loading+=listOfFiles.length;
        listOfFiles = c.getAssets().list(sets_path);
        loading+=listOfFiles.length;
        listOfFiles = c.getAssets().list(maps_path);
        loading+=listOfFiles.length;
    }catch(Exception e){
            e.printStackTrace();
    }finally{
        FragmentManager fm = getFragmentManager();

        if (savedInstanceState == null) {
            Log.d("MainActivity", "onCreate savedInstanceState null");
  //          adapter = new ChatArrayAdapter(getApplicationContext());

            dialog = new BrainLoggerDialog();
            if (!ChatBotApplication.isBrainLoaded()) {
//                dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);

            } else {
                dialog.setPositiveButtonEnabled(true);
            }

        } else {
            Log.d("MainActivity", "onCreate savedInstanceState NOT null");
            dialog = (BrainLoggerDialog) fm.findFragmentByTag(FRAGMENT_DIALOG_LOG_TAG);
        }
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
        actuelContext= getApplicationContext();
        //sp = actuelContext.getSharedPreferences(USER_PREFS,actuelContext.MODE_PRIVATE);
        //pageActuel=sp.getInt("pageActuel", 0);    
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status){
        
            textToSpeech.setLanguage(Locale.US);
            }
        });





        if(tutorial==false)
            textToSpeech.speak("Hi, i am echo", TextToSpeech.QUEUE_FLUSH, null);




        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);

        mMessageReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);

        if (dialog != null && ChatBotApplication.isBrainLoaded()) {

            dialog.loadLog();
            dialog.setPositiveButtonEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_log) {
            FragmentManager fm = getFragmentManager();
            dialog = new BrainLoggerDialog();
            dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    // Broadcast receiver for receiving status updates from the IntentService

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_STATUS)) {

                int status = intent.getIntExtra(Constants.EXTRA_BRAIN_STATUS, 0);
                switch (status) {

                    case Constants.STATUS_BRAIN_LOADING:
                        Toast.makeText(MainActivity.this, "brain loading", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                          //  dialog.show(getFragmentManager(), FRAGMENT_DIALOG_LOG_TAG);
                        }
                        break;

                    case Constants.STATUS_BRAIN_LOADED:
                        Toast.makeText(MainActivity.this, "brain loaded", Toast.LENGTH_SHORT).show();
                         tv1.setText("Hi I'm Echo!");
                     
                        if (dialog != null) {
                            dialog.setPositiveButtonEnabled(true);
                        }
                        break;

                }
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);
                mPager.setCurrentItem(1);

            	edt.setHint("Tell me something"); 
                messageReceive.setText(answer);  
                textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null);                       
              //  adapter.add(new ChatMessage(true, answer));
                //adapter.notifyDataSetChanged();
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_LOGGER)) {

                String info = intent.getStringExtra(Constants.EXTENDED_LOGGER_INFO);
                if (info != null) {
                    Log.i("EXTENDED_LOGGER_INFO", info);
                    if (dialog != null) {
                        dialog.addLine(info);
                    }

                    if(info.contains("Reading")){
                        loaded++;
                        float fvar = ((float)loaded/(float)loading)*(float)100;
                        String str = String.valueOf(fvar);
                        tv1.setText(str+"%");
                     }
                }

            }


        }
    }
    private void setCurrentInflateItem(int type){
        if(type==0){
            mPager.setCurrentItem(0);
            pageActuel=0;

            if(tutorial==false)
                textToSpeech.speak("Hi i am echo", TextToSpeech.QUEUE_FLUSH, null);
        }else if(type==1){
            mPager.setCurrentItem(1);
            pageActuel=1;

            if(tutorial==false)
                textToSpeech.speak("Do you want to talk to me?", TextToSpeech.QUEUE_FLUSH, null);
        }else if(type==2){
            mPager.setCurrentItem(2);
            pageActuel=2;
            if(tutorial==false)
                textToSpeech.speak("Tell me something", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    



    private View.OnClickListener mPagerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            
        switch (v.getId()) {
        case R.id.btn_click:
            setCurrentInflateItem(1);
            pageActuel=1;
            if(tutorial==false)
                textToSpeech.speak("Do you want to talk to me?", TextToSpeech.QUEUE_FLUSH, null);
            break;
        case R.id.btn_click2:
            pageActuel=2;
            setCurrentInflateItem(2);
            if(tutorial==false)     
                textToSpeech.speak("Tell me something", TextToSpeech.QUEUE_FLUSH, null);
            tutorial=true;          
            break;
        }
    }
    };



    private class PagerAdapterClass extends PagerAdapter{
        
        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }
        
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            if(position==0){
                v = mInflater.inflate(R.layout.inflate_one, null);
                tv1=(TextView)v.findViewById(R.id.textView1);
                v.findViewById(R.id.btn_click).setOnClickListener(mPagerListener);
                
            }
            else if(position==1){
                v = mInflater.inflate(R.layout.inflate_two, null);
                messageReceive= (TextView)v.findViewById(R.id.textView2);
                v.findViewById(R.id.btn_click2).setOnClickListener(mPagerListener);
            }else if(position==2){
                v = mInflater.inflate(R.layout.inflate_three, null);
                edt=(EditText)v.findViewById(R.id.messageText2);
                edt.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                   public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {
               }

                @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(before==0 && count==1 && s.charAt(start)=='\n') {
				    String line = null;
            		String input = null;
            		EditText tmptext = (EditText) findViewById(R.id.messageText2);
           			input=tmptext.getText().toString();
            		tmptext.setText(""); 
            		tmptext.setHint("I'am thinking"); 

            		MyInterpreter intpr= new MyInterpreter();
            		if(intpr.estCommande(input)==intpr.CMD_DEBUG){
                	Toast toast4 = Toast.makeText(actuelContext,"debug here", Toast.LENGTH_SHORT);      
                	toast4.show();
		   
            }else if(intpr.estCommande(input)==intpr.CMD_SMS){
                    mPager.setCurrentItem(1);
        
                textToSpeech.speak("Give me your message", TextToSpeech.QUEUE_FLUSH, null);
                Intent intentSubActivity = new Intent(MainActivity.this, SMSActivity.class);
                startActivity(intentSubActivity);

            
            }else if(intpr.estCommande(input)==intpr.CMD_CALL){
                            mPager.setCurrentItem(1);
            ArrayList<ContactsStructure> listContact=getContactsDataAll(getApplicationContext());
            ContactsStructure find;
            int i;
            int len=listContact.size();
                find=listContact.get(0);
                i=0;
            do{
                find=listContact.get(i);
                if(input.toLowerCase().contains(find.str_Display_Name.toLowerCase()))break;

                i++;
            }while(i<len);

            if(i!=len){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+find.str_phone_number));
            messageReceive.setText("I call this person");   
            textToSpeech.speak("I call this person", TextToSpeech.QUEUE_FLUSH, null);
            startActivity(intent);
            }else{
            messageReceive.setText("I don't know this person"); 
            textToSpeech.speak("I don't know this person", TextToSpeech.QUEUE_FLUSH, null);
            }

            }else if(intpr.estCommande(input)==intpr.CMD_MUSICSTART){

                            mPager.setCurrentItem(1);
            if(playingMusic==false){
                messageReceive.setText("I play music"); 
                textToSpeech.speak("I play music", TextToSpeech.QUEUE_FLUSH, null);
                startService(new Intent("com.service.test"));
                playingMusic=true;
            }else{
                messageReceive.setText("You want to change this music? say stop");  
                textToSpeech.speak("You want to change this music? say to me stop", TextToSpeech.QUEUE_FLUSH, null);
            }
            

            }else if(intpr.estCommande(input)==intpr.CMD_MUSICSTOP){
                    mPager.setCurrentItem(1);
        
                if(playingMusic==true){
                    messageReceive.setText("I stop :)");    
                    textToSpeech.speak("I stop", TextToSpeech.QUEUE_FLUSH, null);
                    stopService(new Intent("com.service.test"));
                    playingMusic=false;
                }else{
                    messageReceive.setText("I didn't play music");  
                    textToSpeech.speak("I didn't play music", TextToSpeech.QUEUE_FLUSH, null);
                }



            }else if(intpr.estCommande(input)==intpr.SEND_SERVER){
                

                    Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
                    brainIntent.setAction(BrainService.ACTION_QUESTION);
                    brainIntent.putExtra(BrainService.EXTRA_QUESTION, input);
                    startService(brainIntent);
                    tmptext.setText("");    
                    messageReceive.setText("");      
  
                /*
                try{
                    messageReceive.setText("Wait...");      
                    Socket sock = new Socket(serverExternIP, serverExternPort);
                    OutputStream out = sock.getOutputStream();
                    InputStream in = sock.getInputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    pw.println(input);
                    pw.flush();
                    while((line = br.readLine()) != null)
                        break;
                    pw.close();
                    br.close();
                    sock.close();
                    tmptext.setText("");    
                    messageReceive.setText(line);
                    textToSpeech.speak(line, TextToSpeech.QUEUE_FLUSH, null);
                    tmptext.setHint("Tell me something");
                }catch(Exception e){
                    messageReceive.setText("My server is sleeping");
                    textToSpeech.speak("My server is sleeping", TextToSpeech.QUEUE_FLUSH, null);
                } 
                */          
            }



                        }
                    }
                });
            
            
           

            }
            
            ((ViewPager)pager).addView(v, 0);
            
            return v; 
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {    
            ((ViewPager)pager).removeView((View)view);
        }
        
        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj; 
        }

        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
    }
    
    //////////////// voice recognition
        
    private RecognitionListener listener = new RecognitionListener() {

    @Override
    public void onReadyForSpeech(Bundle params) {
    }
    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
    }
    @Override
    public void onResults(Bundle results) {
    String key= "";
                    
    key = SpeechRecognizer.RESULTS_RECOGNITION;
    ArrayList<String> mResult = results.getStringArrayList(key);
    String[] rs = new String[mResult.size()];
    mResult.toArray(rs);
    EditText tmptext = (EditText) findViewById(R.id.messageText2);
    tmptext.setText("" + rs[0]);


    }
    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }
    };  
    //////////////// voice recognition




    //////////////////////////// get contact  ////////////////////////////////
    public ArrayList<ContactsStructure> getContactsDataAll (Context context){
        ArrayList<ContactsStructure> list = new ArrayList<ContactsStructure>();
        Uri uri_contacts = ContactsContract.Contacts.CONTENT_URI;
        String[] str_contactProjection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,};

        Cursor cursor_contacts = context.getContentResolver().query(uri_contacts, str_contactProjection, null, null, null);

        
        if (cursor_contacts.getCount() == 0){
            cursor_contacts.close();
            return null;
        }else{
            cursor_contacts.moveToFirst();
            do{
                ContactsStructure structure = new ContactsStructure();
                String str_ID = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String str_hasPhoneNumber = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                structure.str_ID = str_ID;
                structure.str_Display_Name = cursor_contacts.getString(cursor_contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                structure.str_has_phone_number = str_hasPhoneNumber;
                if (str_hasPhoneNumber.equals("1")){
                    Uri uri_phoneNumber = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String str_selectionPhoneNumber = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= " + str_ID;
                    String[] str_phoneNumberProjection = new String[] {
                    ContactsContract.CommonDataKinds.Phone.TYPE, 
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                    };
                    Cursor cursor_phoneNumber = context.getContentResolver().query(uri_phoneNumber, str_phoneNumberProjection, str_selectionPhoneNumber, null, null);
                    if (cursor_phoneNumber.getCount() == 0){
                        break;
                    }else{
                        cursor_phoneNumber.moveToFirst();
                        structure.str_phone_number_count = String.valueOf(cursor_phoneNumber.getCount());
                        String str_phoneNumberList = null;
                        do{
                            if(str_phoneNumberList == null){
                               str_phoneNumberList = cursor_phoneNumber.getString(cursor_phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }else{
                                str_phoneNumberList = str_phoneNumberList + " / " + cursor_phoneNumber.getString(cursor_phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            structure.str_phone_number = str_phoneNumberList;
                        }while(cursor_phoneNumber.moveToNext());
                    }
                    cursor_phoneNumber.close();
                }else{
                    structure.str_phone_number_count = "0";
                    structure.str_phone_number = null;
                }
                list.add(structure);
            }while(cursor_contacts.moveToNext());
        }
        cursor_contacts.close();
        return list;
    }
}

