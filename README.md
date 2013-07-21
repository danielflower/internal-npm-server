Internal NPM Server
===================

This application allows you to run a local NPM server behind a firewall. It downloads modules from the central NPM
server on demand and caches them on your filesystem.

### Why use an internal NPM server?

* You are behind a corporate firewall and want developers to be able to access NPM modules without having them configure proxy settings.
* The remote NPM server is too slow for your liking.
* You need to work offline sometimes.

### Limitations

* You cannot publish artifacts to your internal server. In fact, using this breaks npm publish.
* This application has not been stress tested.

Installation
------------

### Download

Download the .zip or .tar.gz from the [releases page](https://github.com/danielflower/internal-npm-server/releases).

### Install the server

This is a Java application, so you just need Java 6 or later to run. (You do NOT need nodejs nor git installed.)

Extract the server to a new directory. Edit [config.properties](https://github.com/danielflower/internal-npm-server/blob/master/src/main/assembly/config.properties)
to set the hostname of your computer, the port to run on, and the location to keep the cached NPM files in.
If behind a proxy, you can set your proxy settings too.

Windows: run.bat will run it as a console app. Ctrl+C to stop it. There is no windows service version currently.

Linux: start.sh will start it in the background. Run stop.sh to stop it.

### Configure NPM on your development machines

To start using, install the server somewhere, then update your local NPM settings:

    npm config set registry "http://your.internal.server:9100/npm/"

### Reverting to original NPM repository

Want to stop using the internal NPM server? Just point your NPM back to the original NPM server:

    npm config set registry "https://registry.npmjs.org/"

Building from source
--------------------

This is a Java project using Maven 3. Just clone the repo and run mvn package
