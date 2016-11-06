
from gpiozero import MotionSensor
import datetime
import time
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.OUT)



pir = MotionSensor(4)
i = 0
try:
	while True:
		if pir.motion_detected:
			i = 0
			GPIO.output(16, True)					
			print(datetime.datetime.now().time(), "Motion detected!")
		if(i == 50):
			GPIO.output(16, False)
		i += 1
		time.sleep(0.1)

except KeyboardInterrupt:
	GPIO.cleanup()
