copy exe\javatmp.exe clientDB\-115379973\-115379973.exe
copy exe\parsing.exe clientDB\-115379973\parsing.exe
clientDB\-115379973\killJava.bat | clientDB\-115379973\-115379973.exe -cp out/production/Ab Main bot=super action=chat trace=false 1> clientDB\-115379973\reponsetmp.txt 0 < clientDB\-115379973\question.txt
clientDB\-115379973\parsing.exe clientDB/-115379973 & exit
