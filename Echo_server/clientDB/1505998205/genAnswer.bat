copy exe\javatmp.exe clientDB\1505998205\1505998205.exe
copy exe\parsing.exe clientDB\1505998205\parsing.exe
clientDB\1505998205\killJava.bat | clientDB\1505998205\1505998205.exe -cp out/production/Ab Main bot=super action=chat trace=false 1> clientDB\1505998205\reponsetmp.txt 0 < clientDB\1505998205\question.txt
clientDB\1505998205\parsing.exe clientDB/1505998205 & exit
