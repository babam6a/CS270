from camera import take_picture
from trajectory import cal_z_angle, cal_trajectory
import socket
import struct

d = 30 # distance from target
h_init = 0 # height of model
g = 9.8 # gravity
max_v = 30 # maximum strength of archery

def model(picture) :
    ret = []
    return ret

def make_data() :
    file_name ='capture.jpg' # file_name of capture image
    
    picture = take_picture(file_name)
    cord_list = model(picture) # CNN model

    ans_str = ""

    if len(cord_list) == 0 :
        ans_str = "Nothing"
    
    else :
        for (x, y) in cord_list :
            z_angle = cal_z_angle(d,x)
            (x_angle, v) = cal_trajectory(d, z_angle, h_init, g, y, max_v)
    
            if (x_angle, v) == (0,0) :
                ans_str +=  "Impossible " # cannot shoot
            else :
                ans_str += "%.3f/%.3f/%.3f "%(z_angle, x_angle, v)
    
    return ans_str.rstrip(" ")

def position_check() :
    file_name = "position_check.jpg" # file_name of capture image
    delta = 0.5

    picture = take_picture(file_name)
    cord_list = model(picture) # CNN model

    ans_str = ""

    if len(cord_list) == 0 : # turn too much
        ans_str = "Turn too much"

    else :
        # todo: need to fix for multiple targets
        # for (x, _) in cord_list :

        try :
            (x, _) = cord_list[0]
            if x < delta :
                ans_str = "Yes"
            else :
                ans_str = "%.3f"%cal_z_angle(d,x)

        except : # error occured while calculating
            ans_str = "Error"

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
            print("data: %s"%data)

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
