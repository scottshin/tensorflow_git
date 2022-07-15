#-*-coding:utf-8-*-

#.#!/usr/bin/python 
import sqlite3
import csv 
import sys, getopt
import io
import os
from os import walk


def db_reg(seq, c, input_txt):


    print input_txt

    try:
        fcsv = io.open ( input_txt, 'r', encoding='utf-8' )
    except FileNotFoundError:
        print("File not found. Check the path variable and ", input_txt)
        exit()


    txt_reader = open( input_txt, "r")
    strings = txt_reader.readlines()
    line = len ( strings )
    txt_reader.close()

    
    
    
    print(strings[0])        # header
    attr = strings[0].split('/')
    sno = int(attr[3])          # sno

    regx = "~()[]\r\n\'\" "

    full_lyric = ""

    i = 0
    for row in strings:
        for char in regx:
            row = row.replace(char,"")
        i = i+1
        if i > 7:
            full_lyric += row
            
            
            
#            full_lyric.append( row )


#    print( full_lyric )
    sql = ("INSERT INTO t_lyrics VALUES ({0}, '{1}', \"{2}\" )".format( seq, sno, full_lyric )  )
    print( sql )
    c.execute( sql )



#    csv_reader = csv.DictReader(fcsv)
#    cnt = 0;

#    for row in csv_reader:
#        print(row['sno'])
#        title = row['title'].split('(')[0]
#        title.replace("\"\"\"", "\'"); 
#        title.replace("\"\"", "\'"); 
#        print(title)
#        sql = ("INSERT INTO songlist "
#           "VALUES "
#           "({0}, '{1}', 123, \"{2}\", \"{3}\", \"{4}\", \"{5}\", 0, '{6}', '{7}', '{8}', 'kr', '0')".format( 
#              cnt, row['sno'], title, 
#              row['writer'], row['composer'], 
#              row['singer'], row['date'], 
#              row['genre'],  row['gender'] )  )            
#        print( sql )
#        c.execute( sql )                    
#        cnt += 1;
#    #    c.execute ( "INSERT INTO songlist VALUES( " + cnt + " + ,  + row['sno'] + ", 'ninano', '" +row[title', 'writer', 'composer', 'singer', 1, '2018', 'pop', 'male', 'kr', '0')" )
#    fcsv.close()



#
# begin code 
#
def func(output_db, input_dir):

    print ('input_csv:', input_dir )
    print ('output_db:', output_db )

    if os.path.isfile(output_db):
        print ('Already File exists :' + output_db)
#        exit()

    conn = sqlite3.connect( output_db )
    c = conn.cursor()
    # create table
    c.execute('''CREATE TABLE IF NOT EXISTS t_lyrics ( 
                seq INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
                sno TEXT NOT NULL,
                lyric       TEXT)''')


    f_list = []
    for (dirpath, dirnames, filenames) in walk(input_dir):
        f_list.extend(filenames)
        break;

    
    seq = 0
    for i in f_list:
        db_reg(seq, c, dirpath+'/'+i)
        seq = seq +1







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
        print('\n\n   python ./makedb_songsearch.py <select_db> <input_dir>\n\n')
        exit()

    func( sys.argv[1], sys.argv[2] )

if __name__ == '__main__':
    main()


