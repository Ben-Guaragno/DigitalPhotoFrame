# Digital Photo Frame
This project is intended to create a digital picture frame on a Raspberry Pi with rotating pictures and weather information. This personal project includes other automation, notably syncing with a google drive account with Rclone for photos, and scripts for automatically turning the display on and off at specified times.

Additional integration with a Tesla Powerwall is [available](#tesla-powerwall-integration).

## Requirements

1. At least Java 11
1. At least JavaFX 11

## Setup Instructions

1. Download the photoframeJFX.zip from the latest release.
1. Extract. Archive contains photoframeJFX.jar and an Icons folder.
	1. Icons can be swapped by editing the images in Icons/
1. Place images inside a directory named "photos" at the same level as the photoframe jar.
1. Create a config file with information about timings of picture, weather, and date updates. Also include a Visual Crossing API key and latitude/longitude for weather information.
	1. See sample config for formatting
	1. photoUpdateSec is in seconds, weatherUpdateMin and dateUpdateMin are in minutes
	1. enableDate can be set to false to remove the date from the display
	1. centerPhoto can be set to false to right align photos in the display
1. The following command line arguments must be provided to allow the JVM to run JFX
	1. The location of the JFX library: `--module-path <PATH-TO-JFX>/lib` (replace `<PATH-TO-JFX>` with the path to your JFX library)
	1. The necessary modules: `--add-modules javafx.controls,javafx.fxml`
	1. The location of a config file

## Tesla Powerwall Integration

If you have a Powerwall (likely v2 required) which is configured to use your network as its primary connection type, its gateway can be used to retrieve the current power output of an attached solar installation. After authenticating, visiting `<gateway-IP>/api/meters/solar` should display detailed information about the solar installation. My program specifically uses “instant_power,” which I understand to be the current wattage being produced. Adding the following to the config file will add a display for the current power output of the solar installation. If less than 100 watts are being produced, the energy display is hidden. As of version 20.49, authentication is required for access to the Powerwall API. Currently the email field is not used, so only the password needs to be given.
	
`enableEnergy=true` Enables the energy display
<br/>
`energyUpdate=1` Sets the energy update frequency in minutes
<br/>
`ip=<LOCAL-IP>` Sets the IP of the gateway
<br/>
`password=<POWERWALL-PASSWORD>` Sets the Password for authentication with the Powerwall

<br/><br/>

Weather is powered by Visual Crossing: https://www.visualcrossing.com/
<br/>
Weather icons from [christiannaths](https://github.com/christiannaths)/[Climacons-Font](https://github.com/christiannaths/Climacons-Font) repo
