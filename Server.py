import socket
#import qtpy
#import pyautogui
import numpy as np
import threading
import autopy
"""
width_height = pyautogui.size()
print(width_height)
print(width_height[0])
print(width_height[1])
"""

hostnm = socket.gethostname()
ipaddr = socket.gethostbyname(hostnm)
port = 1235
print("\nIP Address is:", ipaddr)
print("Port is: ", port)

Headersize = 10
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((socket.gethostname(), port))
s.listen(5)

packet = ""
count = 0
received = 0
while True:
    print("Waiting")
    clientsocket, address = s.accept()

    print("Connection from ", address, "has been established!")
    while True:

        data = clientsocket.recv(1).decode("utf-8")


        if 'L' in data:
            pass
            autopy.mouse.click(button=autopy.mouse.Button.LEFT)
        elif 'R' in data:
            pass
            autopy.mouse.click(button=autopy.mouse.Button.RIGHT)

        elif "\n" in data:
            pass
        else:
            packet = packet + data


            # print(packet)
            # print(packet[0], len(packet))
            # print(packet)

            if len(packet) == 16 and packet[0] == "S":
                received +=1
                print(received)
                # print(packet)
                # print(packet)
                # print("data",packet)

                data_split = packet[1:15].split(",")

                data_split = list(map(float, data_split))



                x = -data_split[0]*8
                y = data_split[1]*8

                if count == 10:
                    cur = autopy.mouse.location()
                    autopy.mouse.move(cur[0] + x, cur[1] + y)
                    count = 0
                else:
                    count += 1
                packet = ""




















