from camera import take_picture
from trajectory import cal_z_angle, cal_trajectory
import cv2 as cv
from roboflow import Roboflow
import socket
import struct

# need internet access to load the model
# 2 projects with 3 models each -> a total of 6 models
# pick the one that suits best

apiKey = ["bzK1X042pq2SWqOQS9t0", "VPr4wYYv2Q7tB49pzRqA"][1] # 2 projects available
version = [1,2,3][2] # both project has total of 3 versions

rf = Roboflow(api_key=apiKey)
projectName = "cs270-team-8"+ ("-mkjrf" if ("VPr4wYYv2Q7tB49pzRqA" == apiKey) else "")
project = rf.workspace().project(projectName)
model_s = project.version(version).model

d = 0.9 # distance from target
h_init = 0.18 # height of model
g = 9.8 # gravity
max_v = 5 # maximum strength of archery
pic_size = (1280,720)
debug = False

def model(picture, w, h) :
    #print("begin model")
    # when confidence is low, more predictions appear making it likely to predict false results
    # when confidence is high, less predictions appear making it likely to pick out true results
    # overall, picking the right confidence is essential for prediction
    result = model_s.predict(picture, confidence=30, overlap=30).json()
    #print(result)

    ret = []
    if debug :
        return [(1,1)]
    # make cordinate from center of the picture
    if len(result['predictions']) == 0 :
        return ret

    ret.append( (int(result['predictions'][0]['x']) - (w//2), -int(result['predictions'][0]['y'] - (h//2)) ))
    #print("x: %d, y: %d"%(ret[0][0], ret[0][1]))

    return ret

def make_data() :
    file_name ='capture.jpg' # file_name of capture image

    picture = take_picture(file_name, pic_size)
    #print("picture")
    cord_list = model(picture, pic_size[0], pic_size[1]) # CNN model
    #print("model")

    ans_str = ""

    if len(cord_list) == 0 :
        #print("Nothing")
        ans_str = "Nothing"

    else :
        for (x, y) in cord_list :
            if debug :
                i = input("test(z,x,v): ").split(" ")
                (z_angle, x_angle, v) = (float(i[0]), float(i[1]), float(i[2]))
                #print("z: %.3f, x: %.3f, v: %.3f"%(z_angle, x_angle, v))
            else :
                z_angle = cal_z_angle(d, x, pic_size[0])
                #print("z_angle")
                (x_angle, v) = cal_trajectory(d, z_angle, h_init, g, y, pic_size[1], max_v)
                #print("x_angle")

            if (x_angle, v) == (0,0) :
                #print("Impossible")
                ans_str +=  "Impossible " # cannot shoot
            else :
                ans_str += "%.3f/%.3f/%.3f "%(z_angle, x_angle, v)
                #print(ans_str)

    return ans_str.rstrip(" ")

def position_check() :
    file_name = "position_check.jpg" # file_name of capture image
    delta = 0.2

    picture = take_picture(file_name, pic_size)
    cord_list = model(picture, pic_size[0], pic_size[1]) # CNN model
    #print("cord_list:")
    #print(cord_list)

    ans_str = ""

    if len(cord_list) == 0 :
        ans_str = "Yes"
        #print(ans_str)

    else :
        # todo: need to fix for multiple targets
        # for (x, _) in cord_list :

        try :
            (x, _) = cord_list[0]
            z_angle = cal_z_angle(d,x,pic_size[0])
            if abs(z_angle) < delta :
                #print(z_angle)
                ans_str = "Yes"
            else :
                if debug :
                    ans_str = "Yes"
                else :
                    ans_str = "%.3f"%z_angle

        except : # error occured while calculating
            ans_str = "Error"
            #print(ans_str)
    #print(ans_str)

    return ans_str

if __name__ == "__main__" :
    server_address = "10.0.1.3"
    port = 8040
    size = 1024

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.bind((server_address, port))
    sock.listen(1)

    print("Waiting ev3Client...")

    try:
        client, clientInfo = sock.accept()
        print("Connected client", clientInfo)

        while True:
            origin_data = b""
            while True :
                origin_data += client.recv(size)
                if b"\n" in origin_data :
                    break

            data = origin_data.decode().rstrip("\n")
            #print("data: %s"%data)

            if "Client ready" in data :
                data = make_data()
                data = bytearray(data, "utf8")
                size = len(data)
                client.sendall(struct.pack("!H", size))
                client.sendall(data)

            elif "Position check" in data :
                send = position_check()
                send = bytearray(send, "utf8")
                size = len(send)
                client.sendall(struct.pack("!H", size))
                client.sendall(send)

            elif "Client finished" in data :
                print("Finish program")
                client.close()
                sock.close()
                break
            elif data :
                print("Wrong response")
                continue
            else:
                print("Disconnected")
                client.close()
                sock.close()
                break
    except:
        print("Closing socket")
        client.close()
        sock.close()
