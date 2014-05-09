'''
Created on 16.03.2014

@author: Denis
'''

import json

BASE_FILE_NAME = "scenes.json"
    
class Scenes(object):
    '''
    handle scenes
    '''

    class Scene(object):
        '''
        handle one scene
        '''
        
        __id = ""
        __def = ""
        __controller = ""
        
        def __init__(self, id, sc_def, ctrl):
            self.__id = id
            self.__def = sc_def
            self.__controller = ctrl
            
        def set_id(self, id):
            self.__id = id
        
        def set_def(self, sc_def):
            self.__def = sc_def
        
        def set_controller(self, ctrl):
            self.__controller = ctrl

        def get_id(self):
            return self.__id
        
        def get_def(self):
            return self.__def
        
        def get_controller(self):
            return self.__controller
        
        def get_list(self):
            sc_l = {'id':self.__id, 'def':self.__def, 'ctrl':self.__controller}
            return sc_l
        
        def get_json(self):
            return (json.dumps(self.get_list(), indent=2, ensure_ascii=False))
        
        def to_JSON(self):
            self.get_json()
    
    __scs = {}

    def __init__(self):
        '''
        Constructor
        '''
    
    def add_scene(self, id, sc_def, ctrl):
        sc = {'def':sc_def, 'ctrl':ctrl}
        self.__scs[id]=sc
    
    def get_scene(self, id):
        sc = self.__scs[id]
        j = json.dumps(sc, indent=2, ensure_ascii=False)
        return j
    
    def to_JSON(self):
        return json.dumps(self.__scs, indent=2, ensure_ascii=False)
    
    def serialize(self):
        f = open(BASE_FILE_NAME, mode="w")
        f.write(self.to_JSON())
        f.close()
        pass
    
    def deserialize(self):
        f = open(BASE_FILE_NAME, mode="r")
        json_data = f.read()
        self.__scs.clear()
        self.__scs = json.loads(json_data)
        f.close()
        pass
    
if __name__ == '__main__':
    sc = Scenes()
#    sc.add_scene('hx_led', 'HX лента', '192.168.1.42:5000')
#    sc.add_scene('hx_led2', 'HX лента2', '192.168.1.43:5000')
    sc.deserialize()
    print(sc.get_scene('hx_led'))
#    sc.serialize()
    