# LibBL - Life is better Bukkit less
LibBL is a system designed to help developers create packet based work without the hassle of managing it.

This tool is primarily designed for Entity and Block packets related work, but as time goes on, more features will be added.

The goal of this project is to provide a long-lasting, easy to use, and efficient way to manage packets in Minecraft plugins without having everyone trying to reinvent the wheel.

Check Latest versions here : http://nexus.rivrs.vpn/#browse/browse:rivrs-snapshot:io%2Frivrs%2FLibBl

## Gradle Dependency
```gradle
repositories {
    maven {
        url = property("rivrsNexusUrl") + "/repository/rivrs-snapshot/"
        allowInsecureProtocol = true
        credentials {
            username = property("rivrsNexusUsername")
            password = property("rivrsNexusPassword")
        }
    }
}

dependencies {
    compileOnly 'io.rivrs:LibBl:<version>'
}
```

## Maven Dependency
TODO

## Basic usage example
```Java
// Create a new PacketItemDisplay at a given bukkit location with a given itemStack
this.display = new PacketItemDisplay(location, model);
// Set the display to be viewable by players around it automatically
display.autoViewable(true); 

// Modify the display properties
display.positionRotationInterpolationDelay(20);

// Spawn the display in the world
display.spawn();

// Remove the display from the world
display.despawn();
```