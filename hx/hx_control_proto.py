'''
Created on 23.04.2014

@author: Denis
'''

HX_DYNAMIC = 2
HX_STATIC = 1

class protocol:
		__all = bytearray()
		product = None
		product_mode = None
		static_dynamic = None
		switch_on = None
		bar_no = None
		red = None
		green = None
		blue = None
		speed = None
		pause = None
		dynamic_mode = None
		dynamic_effect = None
		controller = None
		RGB_sort = None
		IC_number = bytearray().fromhex('00 00')
		Reserve = None
		checkValue = None

		def __init__ (self):
			self.product = 0x00
			self.product_mode = 0x01
			self.static_dynamic = 0x01
#			self.switch_on = 0x01
			self.bar_no = 0x64 #0x32
			self.red = 0xff
			self.green = 0x06
			self.blue = 0x20
			self.speed = 0x00
			self.pause = 0x00
			self.dynamic_mode = 0x00
			self.dynamic_effect = 0x00
			self.controller = 0x00
			self.RGB_sort = 0x00
			self.setIC_number(0)
			self.Reserve = 0x00
			self.checkValue = 0x00

		def shortToByteArray(self, paramShort):
			arrayOfByte = bytearray().fromhex('00 00')
			i = 0
			while (i < 2):
				arrayOfByte[i] = ((0xFF & paramShort >> 8 * (-1 + len(arrayOfByte) - i)))
				i = i + 1
			return arrayOfByte;

		def setIC_number(self, paramInt):
			self.IC_number = self.shortToByteArray(paramInt)

		def setCurCheckValue(self, p1, p2, p3, p4, p5, p6):
				self.checkValue = 0x00
				res1 = p5 + p6;
				res1 += p4
				res1 += p3 
				res1 += p2
				res1 += p1
				res1 += 0xff
				if res1 != 0:
					pass
				res0 = res1 % 0xff
				if (res0 == 0):
					res0 = 0xff
				return res0

		def exchangeInt(self, int_arr):
				i = int_arr[0]
				j = int_arr[1]
				k = i & 0xf0
				m = (i & 0x0f) << 4
				n = j & 0xf0
				i1 = j & 0x0f
				i2 = k + (n >> 4)
				i3 = m + i1
				int_arr[0] = i2
				int_arr[1] = i3

		def exchangeBytes(self):
			arrayOfInt = bytearray().fromhex('00 00')
			i = 0x02
			while i < 0x0b:
				arrayOfInt[0] = self.__all[i]
				arrayOfInt[1] = self.__all[0x15 - i]
				self.exchangeInt(arrayOfInt)
				self.__all[i] = arrayOfInt[0]
				self.__all[0x15 - i] = arrayOfInt[1]
				i += 0x01

		def setAll(self):
			head = bytearray().fromhex('9d 62') #-0x63 = 0xff9d
			self.__all = bytearray(20)
			self.__all[0] = head[0]
			self.__all[1] = head[1]
			self.__all[2] = self.product
			self.__all[3] = self.product_mode
			self.__all[4] = self.static_dynamic
			self.__all[5] = self.switch_on
			self.__all[6] = self.bar_no
			self.__all[7] = self.red
			self.__all[8] = self.green
			self.__all[9] = self.blue
			self.__all[10] = self.speed
			self.__all[11] = self.pause
			self.__all[12] = self.dynamic_mode
			self.__all[13] = self.dynamic_effect
			self.__all[14] = self.controller
			self.__all[15] = self.RGB_sort
			self.__all[16] = self.IC_number[0]
			self.__all[17] = self.IC_number[1]
			self.__all[18] = self.Reserve
			self.checkValue = self.setCurCheckValue(self.bar_no, self.red, self.green, self.blue, self.switch_on, self.product_mode)
			self.__all[19] = self.checkValue
			self.printAll()
			self.exchangeBytes()
#			self.printAll()

		def getAll(self):
			return self.__all
	
		def printAll(self):
			print(' '.join('0x{:x}'.format(x) for x in self.__all))
			