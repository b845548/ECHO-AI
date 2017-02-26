copy exe\javatmp.exe clientDB\-2051273098\-2051273098.exe
copy exe\parsing.exe clientDB\-2051273098\parsing.exe
clientDB\-2051273098\killJava.bat | clientDB\-2051273098\-2051273098.exe -cp out/production/Ab Main bot=super action=chat trace=false 1> clientDB\-2051273098\reponsetmp.txt 0 < clientDB\-2051273098\question.txt
clientDB\-2051273098\parsing.exe clientDB/-2051273098 & exit
