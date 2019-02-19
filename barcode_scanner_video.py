from imutils.video import VideoStream
from pyzbar import pyzbar
import numpy as np
import argparse
import datetime
import imutils
import time
import cv2

img_counter = 0

ap = argparse.ArgumentParser()
ap.add_argument("--output", type=str, default="barcodes.csv",
	help="path to output CSV file containing barcodes")
args = vars(ap.parse_args())

# initialize the video stream and allow the camera sensor to warm up
print("[INFO] starting video stream...")
# Use vs = VideoStream(src=1).start() if you are using an external webcam
vs = VideoStream(0).start()
time.sleep(2.0)

# open the output CSV file for writing and initialize the set of barcodes found thus far
csv = open(args["output"], "w")
found = set()



# the loop is set to true in order for the viseo to go on unless a separate instruction is given.
while True:

	frame = vs.read()
	frame = imutils.resize(frame, width=400)

	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

	# computing the Scharr gradient magnitude

	ddepth = cv2.cv.CV_32F if imutils.is_cv2() else cv2.CV_32F
	gradX = cv2.Sobel(gray, ddepth=ddepth, dx=1, dy=0, ksize=-1)
	gradY = cv2.Sobel(gray, ddepth=ddepth, dx=0, dy=1, ksize=-1)

	# subtract the y-gradient from the x-gradient
	gradient = cv2.subtract(gradX, gradY)
	gradient = cv2.convertScaleAbs(gradient)

	# blur and threshold the image
	blurred = cv2.blur(gradient, (5, 5))
	(_, thresh) = cv2.threshold(blurred, 200, 255, cv2.THRESH_BINARY)

	# construct a closing kernel and apply it to the thresholded image
	kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (21, 7))
	closed = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)

	# perform a series of erosions and dilations
	closed = cv2.erode(closed, None, iterations = 4)
	closed = cv2.dilate(closed, None, iterations = 4)

	# find the contours in the thresholded image, then sort the contours by their area, keeping only the largest one
	cnts = cv2.findContours(closed.copy(), cv2.RETR_EXTERNAL,
		cv2.CHAIN_APPROX_SIMPLE)
	cnts = imutils.grab_contours(cnts)
	c = sorted(cnts, key = cv2.contourArea, reverse = True)[0]

	# compute the rotated bounding box of the largest contour
	rect = cv2.minAreaRect(c)
	box = cv2.cv.BoxPoints(rect) if imutils.is_cv2() else cv2.boxPoints(rect)
	box = np.int0(box)

	# draw a bounding box around the detected barcode and display the image
	cv2.drawContours(frame, [box], -1, (0, 255, 0), 3)
	cv2.imshow("frame", frame)
	# cv2.waitKey(0)

	# find the barcodes in the frame and decode each of the barcodes

	key = cv2.waitKey(1) & 0xFF

# if the `q` key was pressed, break from the loop
	if key == ord("q"):
		break
	elif key%256 == 32:
		img_name = "{}.png".format(img_counter)
		cv2.imwrite(img_name, frame)
		print("{} written!".format(img_name))
		img_counter += 1

# close the output CSV file do a bit of cleanup
print("[INFO] cleaning up...")
csv.close()
cv2.destroyAllWindows()
vs.stop()





