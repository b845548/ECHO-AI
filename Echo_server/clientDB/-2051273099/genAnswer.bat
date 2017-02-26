copy exe\javatmp.exe clientDB\-2051273099\-2051273099.exe
copy exe\parsing.exe clientDB\-2051273099\parsing.exe
clientDB\-2051273099\killJava.bat | clientDB\-2051273099\-2051273099.exe -cp out/production/Ab Main bot=super action=chat trace=false 1> clientDB\-2051273099\reponsetmp.txt 0 < clientDB\-2051273099\question.txt
clientDB\-2051273099\parsing.exe clientDB/-2051273099 & exit
