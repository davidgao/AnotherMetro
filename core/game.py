#!/usr/bin/env python3

import threading as th
import time as t

class Game(object):
	def __init__(self, m = None): # m is an 2d array used as map
		# Tweakables
		self.grow_interval = 1 # in seconds
		self.max_growth = 20 # growth to full map
		# Status
		self.growth = 0
		self.grow_timer = None
		# Initialize
		if m is None:
			x = 600
			y = 800
			self.size = (x, y)
			self.roi_base = (int(x * 0.4), int(x * 0.6),
					 int(y * 0.4), int(y * 0.6))
			self.roi = self.roi_base
	
	# Debug
	def dump(self):
		print("Growth = %d/%d" % (self.growth, self.max_growth))
		print("ROI = (%d, %d, %d, %d)" % self.roi)
	
	# Control
	def grow_start(self):
		timer = th.Timer(g.grow_interval, self.grow)
		self.grow_timer = timer
		timer.start()
	
	def grow_stop(self):
		timer = self.grow_timer
		if timer is not None:
			timer.cancel()
			self.grow_timer = None
	
	# Inner Logic
	def grow(self):
		self.growth += 1
		self.refresh_roi()
		if self.growth == self.max_growth:
			self.grow_stop()
		else:
			self.grow_start()
	
	def refresh_roi(self):
		x1, x2, y1, y2 = self.roi_base
		x, y = self.size
		rate = self.growth / self.max_growth
		delta = 1 - rate
		x1 = int(x1 * delta)
		x2 = int(x * rate + x2 * delta)
		y1 = int(y1 * delta)
		y2 = int(y * rate + y2 * delta)
		self.roi = (x1, x2, y1, y2)

print("Hello, locomotive!")
g = Game()

g.grow_start()

t.sleep(20)

g.grow_stop()

