# Digital Photo Frame
This project is intended to create a digital photo frame on a Raspberry Pi. This personal project includes other automation, notably syncing with a google drive account with Rclone for photos and automatically turning the display on and off at specified times.

## Requirements

1. At least Java 11
1. At least JavaFX 11

## Setup Instructions

1. Download the photoframeJFX.zip from the latest release.
1. Extract. Archive contains photoframeJFX.jar and an Icons folder.
	1. Icons can be swapped by editing the images in Icons/
1. Place images inside a directory named "photos" at the same level as the photoframe jar.
1. Create a config file with information about timings of picture, weather, and date updates. Also include a Dark Sky API key and latitude/longitude for weather information.
	1. See sample config for formatting
	1. photoUpdateSec is in seconds, weatherUpdateMin and dateUpdateMin are in minutes
	1. enableDate can be set to false to remove the date from the display
	1. centerPhoto can be set to false to right align photos in the display
1. The following command line arguments must be provided to allow the JVM to run JFX
	1. The location of the JFX library: `--module-path <PATH-TO-JFX>/lib` (replace `<PATH-TO-JFX>` with the path to your JFX library)
	1. The necessary modules: `--add-modules javafx.controls,javafx.fxml`
	1. The location of a config file

<br/><br/>

Weather is powered by Dark Sky: https://darksky.net/poweredby/
