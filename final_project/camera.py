from picamera import PiCamera
from time import sleep

def take_picture(file_name, pic_size) :
    camera = PiCamera()
    sleep(2)
    camera.rotation = 180
    camera.resolution = pic_size
    camera.capture(file_name)
    camera.close()
    return file_name