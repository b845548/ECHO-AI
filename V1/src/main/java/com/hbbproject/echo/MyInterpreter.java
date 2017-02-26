package com.hbbproject.echo;

public class MyInterpreter{


public MyInterpreter(){};
public static final int SEND_SERVER=0;
public static final int CMD_CALL=1;
public static final int CMD_MUSICSTART=2;
public static final int CMD_MUSICSTOP=3;
public static final int CMD_DEBUG=4;
public static final int CMD_VIDEOMUSIC=5;
public static final int CMD_VIDEOFUN=6;
public static final int CMD_VIDEOMOVIE=7;
public static final int CMD_SMS=8;
public int estCommande(String phrase){
	if(phrase.toLowerCase().contains("dbg"))
		return CMD_DEBUG;
	else if(phrase.toLowerCase().contains("call"))
		return CMD_CALL;
	else if(phrase.toLowerCase().contains("music")&&(phrase.toLowerCase().contains("listen")||phrase.toLowerCase().contains("play")))
		return CMD_MUSICSTART;
	else if(phrase.toLowerCase().contains("stop")||(phrase.toLowerCase().contains("music")&&phrase.toLowerCase().contains("stop")))
		return CMD_MUSICSTOP;
	else if(phrase.toLowerCase().contains("sms")||phrase.toLowerCase().contains("send message"))
		return CMD_SMS;
	else if(phrase.toLowerCase().contains("trailer")||phrase.toLowerCase().contains("movie")&&(phrase.toLowerCase().contains("video")||phrase.toLowerCase().contains("youtube")))
		return CMD_VIDEOMOVIE;
	else if(phrase.toLowerCase().contains("music")&&(phrase.toLowerCase().contains("video")||phrase.toLowerCase().contains("youtube")))
		return CMD_VIDEOMUSIC;
	else if(phrase.toLowerCase().contains("fun")&&(phrase.toLowerCase().contains("video")||phrase.toLowerCase().contains("youtube")))
		return CMD_VIDEOFUN;
	else
		return SEND_SERVER;
}


}