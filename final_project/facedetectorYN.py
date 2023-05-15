# READ THIS
# Made based on code in class
# img is the image the facedetector wants to detect.
# h,w is the height and width of the image

detector = cv.FaceDetectorYN.create("face_detection_yunet_2022mar.onnx", "", (h,w))

while True:
    js_reply = video_frame(label_html, bbox)
    if not js_reply:
        break

    # convert JS response to OpenCV Image
    img = js_to_image(js_reply["img"])

    # Read image
    img_W = int(img.shape[1])
    img_H = int(img.shape[0])

    # Set input size
    detector.setInputSize((img_W, img_H))

    # Getting detections
    detections = detector.detect(img)

    # create transparent overlay for bounding box
    bbox_array = np.zeros([480,640,4], dtype=np.uint8)

    # get face bounding box for overlay
    _, faces = detections
    if faces is not None:
      for arr in faces:
        x = arr[::2]
        y = arr[1::2]
        cv.rectangle(img,(np.uint32(x[0]),np.uint32(y[0])),
                                (np.uint32(x[0])+np.uint32(x[1]),np.uint32(y[0])+np.uint32(y[1])),(255,0,0),2)
