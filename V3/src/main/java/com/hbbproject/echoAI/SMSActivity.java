package com.hbbproject.echoAI;

import android.text.TextWatcher;
import android.text.Editable;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter; 
import android.widget.Button;
import android.database.Cursor; 
import android.view.View;
import android.widget.ArrayAdapter; 
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.net.Uri;
import android.telephony.SmsManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.ActivityManager;
import java.io.File; 
import java.util.ArrayList;

public class SMSActivity extends Activity{
    /** Called when the activity is first created. */
   	private Button sms_button2;		
   	String number=null;
   	int indice=0;
EditText message;
    ArrayList<ContactsStructure> listContact;
    ContactsStructure find;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);
		listContact=getContactsDataAll(getApplicationContext());
        message = (EditText) findViewById(R.id.sms_message);
            
                message.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                   public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {
               }

                @Override
                 public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(before==0 && count==1 && s.charAt(start)=='\n') {
                    String strmessage = message.getText().toString();    
                    if(number!=null&&strmessage.equals("")==false){   
                        Toast.makeText(getApplicationContext(),"sended", Toast.LENGTH_SHORT).show();
                        sendSMS(number,strmessage);
                        finish();
                        }else
            Toast.makeText(getApplicationContext(),"click contact", Toast.LENGTH_SHORT).show();
                        
                        }
                    }
                });
		sms_button2=(Button) findViewById(R.id.contact);
    	sms_button2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
            	find=listContact.get(indice);
				sms_button2.setText(find.str_Display_Name);
    			number=find.str_phone_number;
    			indice++;
    			indice%=listContact.size();	

				}
        });
 

    }
    public void sendSMS(String smsNumber, String smsText){
    PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
    PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);
 
    registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(getResultCode()){
                case Activity.RESULT_OK:
                  //Toast.makeText(mContext, "Send succes", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                  //Toast.makeText(mContext, "Send failed", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
            }
         }
    }, new IntentFilter("SMS_SENT_ACTION"));
 
    registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
         //         Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
//                  Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }, new IntentFilter("SMS_DELIVERED_ACTION"));
 
    SmsManager mSmsManager = SmsManager.getDefault();
    mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
}
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
