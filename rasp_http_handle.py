'''
Created on 08.03.2014

@author: Denis
'''

import logging
from urllib.parse import urlparse, parse_qs
from http.server import BaseHTTPRequestHandler
from users import RaspControllUsers
from raspberry import Raspberry
from hx.hx_controller import hx_controller


LED_BAR_HOST = '192.168.1.42'
LED_BAR_PORT = 5000

class MainRaspHTTPHandler(BaseHTTPRequestHandler):
    __logger = None 
    __users = None
    __rasp = None
    def __init__(self, request, client_address, server):
        self.__users = RaspControllUsers()
        self.__logger = logging.getLogger('rasp-serv')
        self.__rasp = Raspberry()
        print('init')
        BaseHTTPRequestHandler.__init__(self, request, client_address, server)
#        log_hndl = logging.FileHandler('RS_current.log')
#        fmt = logging.Formatter("%(asctime)s %(levelname)s %(message)s")
#        log_hndl.setFormatter(fmt)
#        self.logger.addHandler(log_hndl)
#        self.logger.setLevel(logging.DEBUG)
    
    def log_message(self, format, *args):
        BaseHTTPRequestHandler.log_message(self, format, *args)
        self.__logger.info(format, *args)

    def send_resp(self, resp):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        self.wfile.write(resp.encode('utf-8'))

    def led_bar_handle(self, q):
        hx = hx_controller()
        if q.get('bright'):
            hx.setBright(int(q['bright'][0]))
        if q.get('dynamic'):
            hx.setDynamic(q['dynamic'][0])
        if q.get('red') and q.get('green') and q.get('blue'):
            hx.setRGB(int(q['red'][0]), int(q['green'][0]), int(q['blue'][0]))
        if q.get('speed'):
            hx.setSpeed(int(q['speed'][0]))
        if q.get('pause'):
            hx.setPause(int(q['pause'][0]))
        if q.get('dyn_mode'):
            hx.setDynMode(int(q['dyn_mode'][0]))
        if q.get('dyn_effect'):
            hx.setDynEff(int(q['dyn_effect'][0]))
        hx.send_packet(LED_BAR_HOST, LED_BAR_PORT)

    def do_GET(self):
        self.log_request()
        req = urlparse(self.path.lower())
        q = parse_qs(req.query, keep_blank_values=True)
        if req.path == "/get_token":
            if q.get('andr_id'):
                tok = self.__users.get_token(q['andr_id'][0])
                self.send_resp("TOKEN="+tok)
            else:
                self.send_resp("ERR")
        elif req.path == "/get_sw":
            sw_status = self.__rasp.get_sw_status()
            if sw_status:
                self.send_resp("SW_ON")
            else:
                self.send_resp("SW_OFF")
        elif req.path == "/set_led":
            addr = q['addr'][0]
            status = q['status'][0]
            led = self.__rasp.set_led(addr, status)
            if led:
                self.send_resp("LED_OK")
            else:
                self.send_resp("LED_ERR")
        elif req.path == "/scene":
            pass
        elif req.path == "/led_bar":
            self.led_bar_handle(q)
            self.send_resp("LED_BAR_OK")
        else:
            self.send_resp("ERR")
        pass