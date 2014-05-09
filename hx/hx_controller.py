'''
Created on 23.04.2014

@author: Denis
'''
import socket
from hx_control_proto import *

def static_light(prot):
    prot.product_mode = 1
    prot.static_dynamic = 1
    prot.red = 0x2f
    prot.green = 0x2f
    prot.blue = 0x2f
    prot.bar_no = 0x64
    prot.speed = 0x01
    prot.dynamic_mode = 0x00
    prot.dynamic_effect = 0x00
    prot.RGB_sort = 0
    return

def dynam_light(prot):
    prot.static_dynamic = 2
    prot.red = 0
    prot.green = 0
    prot.blue = 0
    prot.bar_no = 0
    prot.speed = 2
    prot.pause = 0
    prot.dynamic_mode = 1
    prot.dynamic_effect = 3
    prot.RGB_sort = 0
    return

class hx_controller:
    p = protocol()
    def __init__ (self):
        self.p.product_mode = 1
        self.p.RGB_sort = 0
        pass        

    def setDynamic(self, dynamic):
        if dynamic.lower() == 'true':
            self.p.static_dynamic = HX_DYNAMIC
            self.p.red = 0
            self.p.green = 0
            self.p.blue = 0
            self.p.bar_no = 0
            self.p.pause = 0
            self.p.RGB_sort = 0
        else:
            self.p.static_dynamic = HX_STATIC

    def setRGB(self, red, green, blue):
#TODO: it's color meshup        
        self.p.red = green
        self.p.green = red
        self.p.blue = blue

    def setBright(self, bright):
        self.p.bar_no = bright # percent 0 - 100

    def setSpeed(self, speed):
        self.p.speed = speed
        
    def setPause(self, pause):
        self.p.pause = pause
        
    def setDynMode(self, mode):
        self.p.dynamic_mode = mode
        
    def setDynEff(self, eff):
        self.p.dynamic_effect = eff

    def setSwitch(self, state):
        if state.lower() == 'true':
            self.p.switch_on = 1
        else:
            self.p.switch_on = 0

    def send_packet(self, host, port):
        self.p.setAll()
        b = self.p.getAll()
#        self.p.printAll()
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect((host, port))
        s.sendall(b)
#    data = s.recv(1024)
        s.close()
#        print ('Received')
    