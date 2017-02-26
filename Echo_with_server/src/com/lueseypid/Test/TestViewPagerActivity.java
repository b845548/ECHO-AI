package com.lueseypid.Test;
import android.app.Activity;
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
import java.io.File; 
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

public class TestViewPagerActivity extends Activity /*implements OnClickListener*/{

	public Context actuelContext;
	public ViewPager mPager;
	public TextView messageReceive;
	public TextView tv1;

	static boolean playingMusic=false;	
	static boolean tutorial=false;
	static int pageActuel=0;

	//////////// Database 
    DatabaseHelper dbHelper;
	//////////////// Server TCP IP
	final String serverIP ="192.168.1.92";
	final String serverExternIP="http://hbbproject.pagekite.me/";
	final int serverPort = 8000;
	final int serverExternPort = 8000;

	
	//////////////// voice recognition
	public SpeechRecognizer mRecognizer;
	public Intent recognizerIntent;	

	//////////////// voice synthetizer
	public TextToSpeech textToSpeech;

	// SharedPreference
	final String USER_PREFS="SUASDXZ";
	public SharedPreferences sp;
	public SharedPreferences.Editor editor;
	



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tv1 =(TextView)findViewById(R.id.textView1);
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));
		actuelContext= getApplicationContext();
		sp = actuelContext.getSharedPreferences(USER_PREFS,actuelContext.MODE_PRIVATE);
		pageActuel=sp.getInt("pageActuel", 0);	
		textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status){
		
			textToSpeech.setLanguage(Locale.US);
			}
		});
		if(tutorial==false)
			textToSpeech.speak("Hi, i am echo bot", TextToSpeech.QUEUE_FLUSH, null);
//		dbHelper= new DatabaseHelper(getApplicationContext(), "Conversation.db", null, 1);

	}

	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences pref = getSharedPreferences(USER_PREFS, 0);
		SharedPreferences.Editor edit = pref.edit();
		mPager.setCurrentItem(pageActuel);
		edit.putInt("pageActuel",pageActuel);
		edit.commit(); 		
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		pageActuel=sp.getInt("pageActuel", 0);
		mPager.setCurrentItem(pageActuel);
	}

	@Override
	protected void onStart(){
		super.onStart();	
		pageActuel=sp.getInt("pageActuel", 0);
		mPager.setCurrentItem(pageActuel);
	}

	@Override
	protected void onResume() {
		super.onResume();
		pageActuel=sp.getInt("pageActuel", 0);
		mPager.setCurrentItem(pageActuel);
	}



	private void setCurrentInflateItem(int type){
		if(type==0){
			mPager.setCurrentItem(0);
			pageActuel=0;

			if(tutorial==false)
				textToSpeech.speak("Hi i am echo bot", TextToSpeech.QUEUE_FLUSH, null);
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
		case R.id.send:
			mPager.setCurrentItem(1);
			String line = null;
			String input = null;
			EditText tmptext = (EditText) findViewById(R.id.messageText2);
			input=tmptext.getText().toString();
			tmptext.setText("");		

			long now = System.currentTimeMillis();
       		Date date = new Date(now);
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        	//dbHelper.insert(simpleDateFormat.format(date),input);


			MyInterpreter intpr= new MyInterpreter();
			if(intpr.estCommande(input)==intpr.CMD_DEBUG){
			    Toast toast4 = Toast.makeText(actuelContext,"debug here", Toast.LENGTH_SHORT);		
				toast4.show();

			}else if(intpr.estCommande(input)==intpr.CMD_VIDEOMUSIC){

    	    Intent i = new Intent(Intent.ACTION_VIEW);
	    	Uri uri = Uri.parse("https://www.youtube.com/results?search_query="+"music+2016");
       		i.setDataAndType(uri, "video/*");
        	startActivity(i);

			}else if(intpr.estCommande(input)==intpr.CMD_VIDEOFUN){

    	    Intent i = new Intent(Intent.ACTION_VIEW);
	    	Uri uri = Uri.parse("https://www.youtube.com/results?search_query="+"funny");
       		i.setDataAndType(uri, "video/*");
        	startActivity(i);



			}else if(intpr.estCommande(input)==intpr.CMD_VIDEOMOVIE){
    	    Intent i = new Intent(Intent.ACTION_VIEW);
	    	Uri uri = Uri.parse("https://www.youtube.com/results?search_query="+"trailer+2016");
       		i.setDataAndType(uri, "video/*");
        	startActivity(i);

			}else if(intpr.estCommande(input)==intpr.CMD_SMS){

				textToSpeech.speak("Give me your message", TextToSpeech.QUEUE_FLUSH, null);
				Intent intentSubActivity = new Intent(TestViewPagerActivity.this, SMSActivity.class);
				startActivity(intentSubActivity);

			
			}else if(intpr.estCommande(input)==intpr.CMD_CALL){
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
			    if(playingMusic==true){
	    	    	messageReceive.setText("I stop :)");	
					textToSpeech.speak("I stop", TextToSpeech.QUEUE_FLUSH, null);
	       	    	stopService(new Intent("com.service.test"));
					playingMusic=false;
				}else{
					messageReceive.setText("I didn't play music");	
					textToSpeech.speak("I didn't play music", TextToSpeech.QUEUE_FLUSH, null);
				}



			}else if(intpr.estCommande(input)==intpr.SEND_SERVER)
				try{
					messageReceive.setText("Wait...");		
					Socket sock = new Socket(serverIP, serverPort);
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
		break;	
		case R.id.voiceRecognition:
			if(actuelContext==null)
				actuelContext=getApplicationContext();
			if(SpeechRecognizer.isRecognitionAvailable(actuelContext)==true){
				if(mRecognizer==null){
					mRecognizer = SpeechRecognizer.createSpeechRecognizer(actuelContext);
					mRecognizer.setRecognitionListener(listener);
					}
				mRecognizer.startListening(recognizerIntent);
			}else{
				Toast toast2 = Toast.makeText(actuelContext,"Your device not support voice recognition", Toast.LENGTH_SHORT);		
				toast2.show();
				textToSpeech.speak("Your device not support voice recognition", TextToSpeech.QUEUE_FLUSH, null);
			}
		break;
		}
	}
	};
	
	/**
	 * PagerAdapter 
	 */
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
    			v.findViewById(R.id.textView1);
    			v.findViewById(R.id.btn_click).setOnClickListener(mPagerListener);
				
    		}
    		else if(position==1){
    			v = mInflater.inflate(R.layout.inflate_two, null);
				messageReceive= (TextView)v.findViewById(R.id.textView2);
    			v.findViewById(R.id.btn_click2).setOnClickListener(mPagerListener);
    		}else if(position==2){
    			v = mInflater.inflate(R.layout.inflate_three, null);
				v.findViewById(R.id.send).setOnClickListener(mPagerListener);
    			v.findViewById(R.id.voiceRecognition).setOnClickListener(mPagerListener);
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














