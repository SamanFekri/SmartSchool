import RPi.GPIO as GPIO
import time
import threading
import datetime
from gpiozero import MotionSensor
import requests
import json

GPIO.setmode(GPIO.BCM)

GPIO.setup(16, GPIO.OUT)
ipir = MotionSensor(4)

GPIO.setup(27,GPIO.OUT)
GPIO.setup(22,GPIO.IN)
GPIO.setup(23,GPIO.OUT)
GPIO.setup(24,GPIO.IN)

humans = 0
count = [0, 0]
mo = False #lamp status

def sendtoserver():
	j = requests.get("http://thingtalk.ir/channels/118/feed.json?key=CSL9Y4UV0XPJSOB7")
	past = j.json()
	s = "http://thingtalk.ir/update?key=CSL9Y4UV0XPJSOB7"
	s += "&field1="
	s += str(humans)
	s += "&field2="
	s += str(int(mo))
	if(past["feeds"] != []):
		x = past["feeds"][len(past["feeds"])-1]    
		for i in range(3,5):
			s += "&field"+str(i)+"="+x["field"+str(i)]
	else:
		s += "&field3=0&field4=0"
	requests.get(s)

						

def ultra( threadName, TRIG_04, ECHO_04	):
	
	global humans
	global count
	print ("humans: ", humans)
	i = 0
	defa = 0;
	start = False
	pre = 0
	while True:

		GPIO.output(TRIG_04, False)
		time.sleep(0.2)

		GPIO.output(TRIG_04, True)

		time.sleep(0.00001)
 
		GPIO.output(TRIG_04, False)
 
		while GPIO.input(ECHO_04)==0:
			pulse_start = time.time()

		while GPIO.input(ECHO_04)==1:
			pulse_end = time.time()
	
		pulse_duration = pulse_end - pulse_start


		distance_04 = pulse_duration * 17150
		distance_04 = round(distance_04, 2)

		#detecting human
		if((pre - distance_04) > 20):
			if(start):
				if((defa-distance_04) > 20):
					count[int(threadName)] += 1
					if(count[0] == count[1]):
						if(threadName == "0"):
							humans -= 1
							if(humans < 0):
								humans = 0
								count = [0,0]
							print("-1")
						else:
							humans += 1
							print("+1")
						sendtoserver()

		pre = distance_04

		#understand default distance
		if(i < 10):
			defa += distance_04
		elif(i == 10):
			start = True
			defa /= 10
			pre = defa
		i += 1
try:
	threading.Thread(target=ultra, args=("0", 23, 24, )).start()
	threading.Thread(target=ultra, args=("1", 27, 22, )).start()
except:
	print ("Error: unable to start thread")		

i = 0
try:
	while True:

		if ((ipir.motion_detected) or (humans>0)):
			i = 0
			if(mo == False):
				mo = True
				sendtoserver()
			GPIO.output(16, True)
			if(humans <= 0):
				print(datetime.datetime.now().time(), "Motion detected!")
		if((i == 17)):
			i = 0
			if(humans == 0):
				GPIO.output(16, False)
				if(mo):
					mo = False
					sendtoserver()
		i += 1
		print(count)
		time.sleep(0.3)

except KeyboardInterrupt:
	print("Errr",humans,"\n\n\n\n\n\n")
	GPIO.cleanup()

