#!/usr/bin/env python3

import threading as th
import time as t
import random as r

class Game(object):
	def __init__(self, m = None): # m is an 2d array used as map
		# Growth
		self.grow_interval = 3 # in seconds
		self.max_growth = 20 # growth to full map
		self.growth = 0
		self.grow_timer = None
		# Site Spawner
		self.site_spawn_interval = 1
		self.site_spawn_tries = 100
		self.site_dist = 10 # in pixels
		self.max_sites = 40
		self.sites = []
		self.site_spawn_timer = None
		# Map
		if m is None:
			x = 600
			y = 800
			self.size = (x, y)
			self.roi_base = (int(x * 0.4), int(x * 0.6),
					 int(y * 0.4), int(y * 0.6))
			self.roi = self.roi_base
		# Lock
		self.lock = th.Lock()
	
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
	
	def site_spawn_start(self):
		timer = th.Timer(g.site_spawn_interval, self.spawn_site_wrapper)
		self.site_spawn_timer = timer
		timer.start()
	
	def site_spawn_stop(self):
		timer = self.site_spawn_timer
		if timer is not None:
			timer.cancel()
			self.site_spawn_timer = None
	
	# Inner Logic
	def grow(self):
		self.lock.acquire()
		self.growth += 1
		# Refresh ROI
		x1, x2, y1, y2 = self.roi_base
		x, y = self.size
		rate = self.growth / self.max_growth
		delta = 1 - rate
		x1 = int(x1 * delta)
		x2 = int(x * rate + x2 * delta)
		y1 = int(y1 * delta)
		y2 = int(y * rate + y2 * delta)
		self.roi = (x1, x2, y1, y2)
		self.lock.release()
		if self.growth == self.max_growth:
			self.grow_stop()
		else:
			self.grow_start()
		print("Growth = %d/%d" % (self.growth, self.max_growth))
		print("ROI = (%d, %d, %d, %d)" % self.roi)
	
	def spawn_site_wrapper(self):
		self.spawn_site()
		if len(self.sites) == self.max_sites:
			self.site_spawn_stop()
		else:
			self.site_spawn_start()
	
	def spawn_site(self):
		def valid():
			for s in self.sites:
				x0, y0 = s
				if (x - x0)^2 + (y - y0)^2 < d^2:
					return False
			return True
		n = self.site_spawn_tries
		d = self.site_dist
		self.lock.acquire()
		x1, x2, y1, y2 = self.roi
		x1 += d
		x2 -= d
		y1 += d
		y2 -= d
		for _ in range(n):
			x = r.randrange(x1, x2)
			y = r.randrange(y1, y2)
			if valid():
				self.sites.append((x, y))
				self.lock.release()
				print("Spawn site : (%d %d)" % (x, y))
				return
		self.lock.release()
		print("Spawn site failed")

print("Hello, locomotive!")
r.seed()
g = Game()

g.spawn_site()
g.spawn_site()
g.spawn_site()

g.grow_start()
g.site_spawn_start()

t.sleep(65)
g.grow_stop()
g.site_spawn_stop()


