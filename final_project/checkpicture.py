import cv2 as cv
from roboflow import Roboflow

# 2 projects with 3 models each -> a total of 6 models
# pick the one that suits best

apiKey = ["bzK1X042pq2SWqOQS9t0", "VPr4wYYv2Q7tB49pzRqA"][1] # 2 projects available
version = [1, 2, 3][2] # both project has total of 3 versions

rf = Roboflow(api_key=apiKey)
projectName = "cs270-team-8" + ("-mkjrf" if ("VPr4wYYv2Q7tB49pzRqA" == apiKey) else "")
project = rf.workspace().project(projectName)
model = project.version(version).model

# the name of the image folder
imageFolder = "KakaoTalk_20230602_163610177.jpg"

# visualize your prediction
result = model.predict(imageFolder, confidence=30, overlap=30).json()

image = cv.imread(imageFolder)

# bounding box of the image
for i in range(len(result['predictions'])):
    image = cv.rectangle(image,
                         (int(result['predictions'][i]['x'] - result['predictions'][i]['width']/2),
                          int(result['predictions'][i]['y'] - result['predictions'][i]['height']/2)),
                         (int(result['predictions'][i]['x'] + result['predictions'][i]['width']/2),
                          int(result['predictions'][i]['y'] + result['predictions'][i]['height']/2)),
                         (0,0,255), 3)

# coordinates for the middle of the head will become as following
# head_middle_x = int(result['predictions'][0]['x']
# head_middle_y = int(result['predictions'][0]['y']

cv.imshow(image)
