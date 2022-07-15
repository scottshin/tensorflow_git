#-*-coding:utf-8-*-

#.#!/usr/bin/python 

import csv 
import sys, getopt
import io
import os

import re           # 정규식을 쓰기위한 :w

#
# begin code 
#

def func(input_csv, output_csv):
    print ('input_csv:', input_csv )
    print ('output_csv:', output_csv )


    # 출력 파일이 이미 존재하는지 검사. 
    if os.path.isfile(output_csv):
        print ('Already File exists :' + output_csv)
        #exit()

    # 파일 읽기보드로 파일 열기
    try:
        fcsv = io.open ( input_csv, 'r', encoding='utf-8' )
    except FileNotFoundError:
        print("File not found. Check the path variable and ", input_csv)
        exit()

    # 파일 생성하기 (쓰기모드로 파일을 생성)
    fwcsv = io.open ( output_csv, 'w', encoding='utf-8', newline='' )

    # 구분자를 탭으로로 하여 라인을 생성한다. 
    wr = csv.writer(fwcsv, delimiter='\t')

    # CSV 포멧으로 파일을 읽는 핸들러를 얻는다. 
    csv_reader = csv.DictReader(fcsv)
    cnt = 1;


    #정규식1. 필요없는 문자 제거  
    #text = u'010-1566#7152'
    #parse = re.sub('[-=.$/?:$}]', '' , text )
    #output : 01015667152

    #정규식2. 괄호 문자 추출 
    #text = "LOG_ADD(LOG_FLOAT,actuatorThurust,&actuatorTrush)"
    #parse = re.findall('\(([^)]+)', text)
    #output : ..


    # 주) 파이선 정규식 이해할것. 
    for row in csv_reader:
        print(row['sno'])
        title = row['title'].split('(')[0]
        title.replace("\"\"\"", "\'"); 
        title.replace("\"\"", "\'"); 
        print(title)
        try:
            m = re.search('\((.*?)\)', row['title'] )
            wr.writerow([cnt, row['sno'], title, m.group(1) ] )
        except:
            wr.writerow([cnt, row['sno'], title, "" ] )
        cnt = cnt +1
    fcsv.close()
    fwcsv.close()

    
    print("job complete..")

def main():
    #print ('Number of arguments:', len(sys.argv) )
    #print ('Argument List:', str(sys.argv) )
    if len(sys.argv) < 2:
        print('\n\n   python ./makeCSV <input_csv> <output_csv>\n\n')
        exit()

    func( sys.argv[1], sys.argv[2] )

if __name__ == '__main__':
    main()


