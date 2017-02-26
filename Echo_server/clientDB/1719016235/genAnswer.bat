copy exe\javatmp.exe clientDB\1719016235\1719016235.exe
copy exe\parsing.exe clientDB\1719016235\parsing.exe
clientDB\1719016235\killJava.bat | clientDB\1719016235\1719016235.exe -cp out/production/Ab Main bot=super action=chat trace=false 1> clientDB\1719016235\reponsetmp.txt 0 < clientDB\1719016235\question.txt
clientDB\1719016235\parsing.exe clientDB/1719016235 & exit
