# Pedometer
#  Special music for code review: https://bit.ly/2S3gNUg
"A pedometer is a device, usually portable and electronic or electromechanical, that counts each step a person takes by detecting the motion of the person's hands or hips. Because the distance of each person's step varies, an informal calibration, performed by the user, is required if presentation of the distance covered in a unit of length (such as in kilometers or miles) is desired, though there are now pedometers that use electronics and software to automatically determine how a person's step varies. Distance traveled (by walking or any other means) can be measured directly by a GPS receiver."
-https://en.wikipedia.org/wiki/Pedometer

#
This is an android app in java (1.8) of a simple pedometer for counting steps taking info from hardware TYPE_STEP_COUNTER target device must have this component. Distance is calculated according to number of steps which is then multiplied by average distance of step (70 cm). The goal was to create app according to photo:

![Alt text](Images/goal.jpg?raw=true "Goal")

# My app
![Alt text](Images/Screenshot_20200926-233802_pedometer.jpg?raw=true "Goal")




# Used classes:

[DatabaseHandler] handles all IO operations with database. App uses one table USER_DATA where are stored data for each date so the statistics can be retrieved after device restart as well.

[DayData] represents on day of data - so it can be easily extended later

[MainActivity] everything else with logic is there. Firstly, application loads data, now in testing mode it randomly generates dummy values for demonstration of graph (as You can see on image). Should work with real methods when used as well.
For graph rendering, library MPAndroidChart was used.
