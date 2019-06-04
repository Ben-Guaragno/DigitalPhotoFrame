# Digital Photo Frame
This project is the java code for a digital photo frame. This personal project includes other automation on a raspberry pi (notably syncing with a google drive account for photos and automatically turning the display on and off at specified times)

Place images inside a directory named "photos" at the same level as the photoframe executable.

Start the program with the name of the config file as a parameter to specify the timings of picture, weather, and date updates. See the sample config file for formatting and parameter names. Note that the photoUpdate time is in milliseconds, while the other two times are in minutes.
