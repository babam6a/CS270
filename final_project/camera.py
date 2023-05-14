from picamera import PiCamera
from time import sleep

def take_picture(file_name) :
    camera = PiCamera()
    sleep(2)
    camera.rotation = 180
    camera.resolution = (1280, 720)
    camera.capture(file_name)
    camera.close()
    return file_name