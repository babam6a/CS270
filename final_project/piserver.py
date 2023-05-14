from camera import take_picture
from trajectory import *
import socket

def model(picture) :
    return (0,0)

def make_data() :
    d = 0 # distance from target
    h_init = 0 # height of model
    g = 9.8 # gravity
    max_v = 30 # maximum strength of archery
    file_name ='capture.jpg' # file_name of capture image
    
    picture = take_picture(file_name)
    (x,y) = model(picture) # CNN model
    
    z_angle = cal_z_angle(d,x)
    (x_angle, v) = cal_trajectory(d, z_angle, h_init, g, y, max_v)
    
    if (x_angle, v) == (0,0) :
        return "Impossible target" # cannot shoot
    else :
        return "%.3f %.3f %.3f"%(z_angle, x_angle, v)
    

if __name__ == "__main__" :
    server_address = "10.0.1.3"
    port = 8040
    size = 1024

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((server_address, port))
    sock.listen(1)

    print("Waiting ev3Client...")

    try:
        client, clientInfo = sock.accept()
        print("Connected client", clientInfo)
        
        while True:
            data = client.recv(size)
            if data == "Client ready" :
                send_data = make(data)
                client.send(send_data)
            elif data == "Client finished" :
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