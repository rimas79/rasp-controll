'''
Created on 08.03.2014

@author: Denis
'''

class RaspUser:
    ID = "8c74b0eed20b680b"
    Name = ""
    Group = -1

class RaspControllUsers(object):
    '''
    provide storing users storing and auth
    '''

    user_list = dict() 

    def __init__(self):
        '''
        Constructor
        '''
        #loading users
        user = RaspUser()
        user.ID = "8c74b0eed20b680b"
        user.Name = "Dennis"
        user.Group = 0 
        self.user_list[user.ID] = user 
    
    def get_token(self, uid):
        tok = 0
        if uid in self.user_list:
            tok = "12345"
        return tok
    
    def get_user_name(self, token):
        return "Dennis"