#-*-coding:utf-8-*-

#.#!/usr/bin/python 



#ref. https://picosanta.tistory.com/13

import numpy as np
import pylab
from scipy.io.wavfile import write
import os

import matplotlib.pyplot as plt

# sampling rate
Fs = 44100.0 # Hz

# play length
tlen = 1 # s
Ts = 1/Fs # sampling interval
t = np.arange(0, tlen, Ts) # time array

# generate signal
sin_freq = 440 # Hz
signal = np.sin(2*np.pi*sin_freq*t)

# generate noise
#noise = np.random.uniform(-1, 1, len(t))*0.1

# signal + noise
#signal_n = signal + noise
signal_n = signal

# fft
signal_f = np.fft.fft(signal_n)
freq = np.fft.fftfreq(len(t), Ts)

#plt.plot(freq, signal_f, freq, signal_f.imag)
#plt.show()


# plot
pylab.plot(freq, 20*np.log10(np.abs(signal_f)))
pylab.xlim(0, Fs/2)


# save as wav file
scaled = np.int16(signal_n/np.max(np.abs(signal_n)) * 32767)
write('test.wav', 44100, scaled)

# play wav file
os.system("start test.wav")


