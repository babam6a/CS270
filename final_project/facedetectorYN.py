# made this with lack of understanding and no decoding; make sure to change the code

from picamera import PiCamera
from time import sleep
import cv2 as cv
import numpy as np

# the width and height of the image. Change accordingly.
camH = 2592
camW = 1944

image = np.empty((camH * camW * 3,), dtype=np.uint8)

camera = PiCamera()
camera.resolution = (camH, camW)

# the model for testing. It detects the faces the picamera intakes.
# face_detection_yunet_2022mar.onnx is necessary
detector = cv.FaceDetectorYN.create("face_detection_yunet_2022mar.onnx", "", (camH, camW))
detector.setInputSize((camH, camW))

while True:
  # part of the code where it capturs with the camera.
  # cv.imread('sth.jpg') can be helpful
  sleep(2)
  camera.capture(image, 'bgr')
  camera.close()
  image = image.reshape((camH, camW, 3)) 

  # get results from the model.
  detections = detector.detect(image)

  # x+w/2 + y+h/2 is the value we want
  x = detections[1][0]
  y = detections[1][1]
  w = detections[1][2]
  h = detections[1][3]

  # if displaying following image, bounding box around the face will appear
  image = cv.rectangle(image,(x,y),(x+w,y+h),(255,0,0),2)

camera.close()
