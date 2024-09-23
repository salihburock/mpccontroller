# mpccontroller
Android app that provides gui for simple mpc commands


There are some stuff you should setup on your own:

Root is required for some features
   To display the info with "mpc" command there is a file located at /sdcard/mpc that contains the output of "mpc" command (I used a simple python loop to update it every second).

MPD must be running on Termux

Termux must have allow-external-apps = true at ~/.termux/termux.properties



It's as simple as it gets for now

![screenshot](https://github.com/salihburock/mpccontroller/blob/main/screenshots/photo_2024-09-23_23-22-06.jpg)
