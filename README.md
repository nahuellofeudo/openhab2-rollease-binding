# <bindingName> Binding

Highly experimental OpenHAB 2 binding for Rollease Acmeda motorized roller shades.

## Supported Things

This binding connects to the Automate Pulse hub and controls Wirefree motors. At this time the only devices confirmed to work are Automate Wirefree tubular motors.

## Discovery

The binding auto-discovers all motorized rollers already registered to the hub. No per-roller configuration is necessary.

## Binding Configuration

Follow these steps to configure the binding:

1) Install all your motorized roller shades. If they are battery operated make sure that the batteries are charged.
2) Install your Automate Pulse near your roller shades. Make sure that the roller shades have good signal.
3) Make sure that your Automate Pulse hub has a static IP address (You may need to configure a static mapping in your router)
4) Using the Rollease phone app, name all your roller shades and configure all the top and bottom positions. 
5) Create a Thing of type Rollease Hub, and configure it with the IP address of your hub.
6) All rollers registered in the hub should automatically appear in your Inbox as new Things. Simply add them to your system.

If you have multiple hubs, repeat steps 3-5 for your other hubs

## Channels

Each roller exposes two channels:

* Position: An integer number for how much the roller is open. A value of 0 means all closed (rolled down) and 100 means all open (rolled up). This value should update if the position of the roller is changed through a remote or through the phone app.
* Battery: What *MAY* be the battery level of the roller. I haven't figured this one out yet. Don't use it.

## Installation

The installation should be straightforward:

1) Run *mvn package*
2) Copy target/org.openhab.binding.rollease-2.x.y.jar to your OpenHAB's *addons* directory

## Caveats

This binding was created by reverse-engineering the communication between an Automate Pulse hub and the Rollease Android app. As such, there are gray areas of the protocol that I haven't figured out yet and in many places I just copied verbatim the bytes that the phone app sends to the Automate hub. 

Overall the binding works well enough for me, and does a reasonable job of compensating the unreliable communication between hub and rollers, and maintaining a reliable connection to the hub through disconnections and protocol loss of sync.

The binding can parse enough of the hub's protocol to be able to set and read rollers' positons, but there are many messages for which there are no parsers. These messages are just logged in hex format and ignored.

Your mileage may vary. Feel free to email me if you have feedback (nlofeudo@gmail.com)
