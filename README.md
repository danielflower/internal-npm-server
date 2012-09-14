Simple Java Web Starter
=======================
Not a framework, rather a little project which can act as the base of Java web projects.  It uses the following technologies:
* HTTP server using http://www.simpleframework.org/
* Velocity for HTML templating generation
* [Zurb Foundation 3.0](http://foundation.zurb.com/)

Static content is served from the webroot folder, 404 and 500 exceptions are handled, model-view-controller is ready
to be used by adding a view to the views folder as a Velocity template and adding new Request Handler.