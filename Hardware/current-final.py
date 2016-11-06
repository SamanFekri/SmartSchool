#!/usr/bin/env python
import time
import RPi.GPIO as GPIO
import urllib2
import math

GPIO.setmode(GPIO.BCM)
# read SPI data from MCP3008 chip, 8 possible adc's (0 thru 7)
def readadc(adcnum, clockpin, mosipin, misopin, cspin):
	if ( (adcnum > 7) or (adcnum < 0) ):
		return -1
	GPIO.output(cspin, True)
 
	GPIO.output(clockpin, False) # start clock low
	GPIO.output(cspin, False) # bring CS low
 
	commandout = adcnum
	commandout |= 0x18 # start bit + single-ended bit
	commandout <<= 3 # we only need to send 5 bits here
	for i in range(5):
		#print ("commandout",commandout)
		if (commandout & 0x80):
			GPIO.output(mosipin, True)
		else:
			GPIO.output(mosipin, False)
		commandout <<= 1
		GPIO.output(clockpin, True)
		GPIO.output(clockpin, False)
 
	adcout = 0
	# read in one empty bit, one null bit and 10 ADC bits
	for i in range(12):
		#time.sleep(0.1)
		GPIO.output(clockpin, True)
		GPIO.output(clockpin, False)
		adcout <<= 1
		#print ("misopin",GPIO.input(misopin))
		#if (GPIO.input(misopin)):
		input = GPIO.input(misopin)
		#print i,"->",input
		if(input):
			adcout |= 0x1
 
	GPIO.output(cspin, True)
 
	adcout >>= 1 # first bit is 'null' so drop it
	return adcout
# change these as desired - they're the pins connected from the
# SPI port on the ADC to the Raspberry Pi</h1>
SPICLK = 18
SPIMISO = 23
SPIMOSI = 24
SPICS = 25
 
GPIO.setwarnings(False)
#set up the SPI interface pins
GPIO.setup(SPIMOSI, GPIO.OUT)
GPIO.setup(SPIMISO, GPIO.IN)
GPIO.setup(SPICLK, GPIO.OUT)
GPIO.setup(SPICS, GPIO.OUT)

###
GPIO.setup(5, GPIO.OUT)

factor= 185.


count = 0
 
try:
	while True:
		#read the analog pin
		analog_value = readadc(pulse_adc, SPICLK, SPIMOSI, SPIMISO, SPICS)
		tmp = analog_value-512
		print ("current:", ((tmp)/factor))
		count += 1

		#send to channel
		s = "http://www.thingtalk.ir/update?key=LY1NL1IP5EHQPLD7&field1="
		s +=str(analog_value)

    	time.sleep(0.2)
	    
			    
except KeyboardInterrupt:
	pass
