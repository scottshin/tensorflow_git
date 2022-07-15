#-*-coding:utf-8-*-

#.#!/usr/bin/python 

import csv 
import sys, getopt
import io
import os

import re           # 정규식을 쓰기위한 :w


#ref. https://wikidocs.net/4309
#     http://blog.naver.com/PostView.nhn?blogId=dudwo567890&logNo=130162403749


# \b 단어 구분자 , 보통 단어는 whitespace에 의해 구분 

p = re.compile(r'\bclass\b')
print(p.search('no class at all'))


print(p.search('the declassified algorithm'))



ip = "[Image]test.png[/Image]"
p = re.search('\[Image\](.*?)\[\/Image\]', ip)
print p.group(1)
