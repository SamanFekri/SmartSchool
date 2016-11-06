import RPi.GPIO as GPIO 
import dht11 
import time 
import datetime 
import requests
import json

# initialize GPIO
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
GPIO.cleanup()

# read data using pin 14
instance = dht11.DHT11(pin=14)
coolerstatus = False
GPIO.setup(16, GPIO.OUT)

while True:
	result = instance.read()
	if result.is_valid():
		
		#send data to server
		j = requests.get("http://thingtalk.ir/channels/117/feed.json?key=7RU9LTSI8X4E8Y0G")
		past = j.json()
		x = past["feeds"][len(past["feeds"])-1]

		sttime = x["field1"]
		sclk, smin = (int(i) for i in sttime.split(':', 1))

		endtime = x["field2"]
		eclk, emin = (int(i) for i in endtime.split(':', 1))

		#cast server time to local time
		currenttime = x["created_at"]
		currenttime = datetime.datetime.strptime(currenttime, "%Y-%m-%dT%H:%M:%SZ")
		currenttime = currenttime + datetime.timedelta(hours=3, minutes=30)
		curmin = currenttime.time().minute
		curclk = currenttime.time().hour

		#checking on & off cooler time with local time
		if((curclk<sclk)or((curclk == sclk) and (curmin<smin))):
			coolerstatus = False
		elif((curclk>eclk)or((curclk == eclk) and (curmin > emin))):
			coolerstatus = False
		else:
			coolerstatus = True
		GPIO.output(16, not(coolerstatus))

		#send data to server
		s = "http://thingtalk.ir/update?key=7RU9LTSI8X4E8Y0G"
		for i in range(1,3):
			s += "&field"+str(i)+"="+x["field"+str(i)]
		s += "&field3="+str(result.temperature)
		requests.get(s)
		time.sleep(1)
