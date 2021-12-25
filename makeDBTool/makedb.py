#-*-coding:utf-8-*-

#.#!/usr/bin/python 

import sqlite3
import csv 
import sys, getopt
import io
import os

#
# begin code 
#

def func(input_csv, output_db):
    print ('input_csv:', input_csv )
    print ('output_db:', output_db )


    if os.path.isfile(output_db):
        print ('Already File exists :' + output_db)
        exit()

    conn = sqlite3.connect( output_db )
    c = conn.cursor()
    # create table
    c.execute('''CREATE TABLE IF NOT EXISTS songlist ( 
                seq INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
                songnum TEXT NOT NULL,
                company NUMERIC,
                title   TEXT,
                writer  TEXT,
                composer    TEXT,
                singer      TEXT,
                album       INTEGER,
                createdate  TEXT,
                genre       TEXT,
                gender      TEXT,
                country     TEXT,
                tieup       TEXT)''')
    try:
        fcsv = io.open ( input_csv, 'r', encoding='utf-8' )
    except FileNotFoundError:
        print("File not found. Check the path variable and ", input_csv)
        exit()

    csv_reader = csv.DictReader(fcsv)
    cnt = 0;
    for row in csv_reader:
        print(row['sno'])
        title = row['title'].split('(')[0]
        title.replace("\"\"\"", "\'"); 
        title.replace("\"\"", "\'"); 
        print(title)
        sql = ("INSERT INTO songlist "
           "VALUES "
           "({0}, '{1}', 123, \"{2}\", \"{3}\", \"{4}\", \"{5}\", 0, '{6}', '{7}', '{8}', 'kr', '0')".format( 
              cnt, row['sno'], title, 
              row['writer'], row['composer'], 
              row['singer'], row['date'], 
              row['genre'],  row['gender'] )  )            
        print( sql )
        c.execute( sql )                    
        cnt += 1;
    #    c.execute ( "INSERT INTO songlist VALUES( " + cnt + " + ,  + row['sno'] + ", 'ninano', '" +row[title', 'writer', 'composer', 'singer', 1, '2018', 'pop', 'male', 'kr', '0')" )
    fcsv.close()
    #c.execute("INSERT INTO stocks VALUES ( '2006-01-05', 'BUY', 'RHAT', 100, 34.14)")
    # Save (commit) the chages 
    conn.commit()
    #
    # close db 
    conn.close()


    print("job complete..")

def main():
    #print ('Number of arguments:', len(sys.argv) )
    #print ('Argument List:', str(sys.argv) )
    if len(sys.argv) < 2:
        print('\n\n   python ./makeDb <input_csv> <output_db>\n\n')
        exit()

    func( sys.argv[1], sys.argv[2] )

if __name__ == '__main__':
    main()


