#include<stdio.h>
#include<string.h>
#include<assert.h>
int i=0;
char str[]={"Human: Robot: "};
int lenstr=14;
int strcmper(char c){
	if(c==str[i])
	i++;
	else i=0;
	
	if(lenstr==i)return 1;
	else return 0; 
}

int main(int args,char ** argv){

int c,prec=0,precprec=0,precprecprec=0;
int print=0;
int cnt=0;
FILE *f,*out;
char str[100];
char str2[100];
strcpy (str,argv[1]);
strcat (str,"/reponse.txt");
out=fopen(str,"w");
assert(out);
strcpy (str2,argv[1]);
strcat (str2,"/reponsetmp.txt");
f=fopen(str2,"r");
assert(f);

while((c=fgetc(f))!=EOF){
if(!print)print+=strcmper(c);
else{
if(print!=0)
if(print!=0&&precprecprec=='\n'&&precprec=='H'&&prec=='u'&&c=='m')break;
if(cnt>1)
fputc(precprec,out);
cnt++;
//printf("%c",c);
}
precprecprec=precprec;
precprec=prec;
prec=c;
}
fclose(f);
fclose(out);
}
