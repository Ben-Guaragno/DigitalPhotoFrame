# Digital Photo Frame
This project is the java code for a digital photo frame. This personal project includes other automation on a raspberry pi (notably syncing with a google drive account for photos and automatically turning the display on and off at specified times)

## Setup Instructions

1. Download the photoframe.jar from the latest release
1. Place images inside a directory named "photos" at the same level as the photoframe executable.
1. Create a config file with information about timings of picture, weather, and date updates. Also include a Dark Sky API key and lattitude/longitude for weather information.
    1. See sample config for formatting
    1. photoUpdate is in milliseconds, weatherUpdate and dateUpdate are in minutes
1. Start the program with the name of the config file as a parameter

(Note: to close the program, either kill it manually or press Escape)

<br/><br/>

Weather is powered by Dark Sky: https://darksky.net/poweredby/
