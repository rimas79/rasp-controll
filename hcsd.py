#!/usr/bin/python3
# -*- coding: utf-8 -*-

'''
Created on 08.03.2014

@author: Denis
'''
import sys
#HCS_PATH="/home/hcs/home-control-server"
HCS_PATH="./"

sys.path.append(HCS_PATH)
print(sys.path)
sys.path.append(HCS_PATH+"/hx")
print(sys.path)

import logging
from rasp_http_handle import MainRaspHTTPHandler
from http.server import HTTPServer

if __name__ == '__main__':
    logger = logging.getLogger('rasp-serv')
    log_hndl = logging.FileHandler(HCS_PATH+'/RS_current.log')
    fmt = logging.Formatter("%(asctime)s %(levelname)s %(message)s")
    log_hndl.setFormatter(fmt)
    logger.addHandler(log_hndl)
    logger.setLevel(logging.DEBUG)
        
    server_address = ('', 8000)
    httpd = HTTPServer(server_address, MainRaspHTTPHandler)
    try:
        logger.info("ServerStart")
        httpd.serve_forever()
    except:
        logger.info("ServerStoping")
        httpd.socket.close()
        logger.info("ServerStop")
    pass
