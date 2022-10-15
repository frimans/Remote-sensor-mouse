import socket
import qtpy
import pyautogui
import numpy as np
import threading


width_height = pyautogui.size()
print(width_height)
print(width_height[0])
print(width_height[1])


hostnm = socket.gethostname()
ipaddr = socket.gethostbyname(hostnm)
port = 1235
print("\nIP Address is:", ipaddr)
print("Port is: ", port)

Headersize = 10
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostname(), port))
s.listen(5)


while True:
    clientsocket, address = s.accept()

    print("Connection from ", address, "has been established!")
    while True:

        data = clientsocket.recv(1000).decode("utf-8")
        print(data)
        data = data.replace('\n', ',')
        data_split = data.split("," )
        if data_split[-1] == '':
            data_split.remove('')
        if 'Left click' in data_split:
            pyautogui.leftClick()
        elif 'Right click' in data_split:
            pyautogui.rightClick()
        else:
            data_split = list(map(float, data_split))
            print(data_split)
            residual = len(data_split) % 3
            if residual !=0:
                data_split = data_split[0:-residual]
            data_3 = np.array(data_split).reshape((int(len(data_split)/3),3))
            #print(data_3)

            # Move the mouse based on the acceleration
            if abs(data_3[0, 0]) < 0.5:
                x = 0.0
            else: x = -data_3[0, 0] * 10

            if abs(data_3[0, 1]) < 0.3:
                y = 0.0
            else: y = data_3[0, 1] * 10

            pyautogui.move(x, y, duration=0)
















