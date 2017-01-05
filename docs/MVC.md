# Model-View-Controller

The famous, or perhaps infamous, Model-View-Controller pattern dictates that code be organized into the groups _models_, _views_, and _controllers_. The exact responsibilities of each group vary in details, and for this reason, this document contains a description of the MVC interpretation used in this project.

## Model

Typically, the models represent the business concerns of a given application. It is completely detached from any notion of visualization or user interaction. In UpCheck, models are all located inside the `se.ltu.dcc.upcheck.model` package. 

Models are never to depend on any classes inside the `se.ltu.dcc.upcheck.view` or `se.ltu.dcc.upcheck.controller` packages.

## View

The view of MVC applications are generally concerned with data presentation, but apart from that different MVC architectures diverge. In UpCheck, views are also concerned with window system event capturing and propagation.

To divide the concerns of _rendering_, _controller interfacing_ and _view behaviour_, the classes in the view package are loosely grouped in three major groups.

__Rendering__ is the process of making some view visible and interactive to the application user. Classes with `Renderer` in their names, or which implement the `Renderer` interface fall into this category.

__Controller Interfacing__ is the exposing of functionality to the application controller. All classes that are accessible outside of the view package fall into this category.

__View Behaviour__ is relevant only to the view itself, and for this reason only available inside the view package.

Views are never to depend on any classes inside the `se.ltu.dcc.upcheck.model` or `se.ltu.dcc.upcheck.controller` packages.

Some significant classes, from an architectural standpoint, are [Renderer][view-renderer], [Renderers][view-renderers], [View][view-view], [Window][view-window] and [WindowView][view-view].

[view-renderer]: ../src/main/java/se/ltu/dcc/upcheck/view/Renderer.java
[view-renderers]: ../src/main/java/se/ltu/dcc/upcheck/view/Renderers.java
[view-view]: ../src/main/java/se/ltu/dcc/upcheck/view/View.java
[view-window]: ../src/main/java/se/ltu/dcc/upcheck/view/Window.java
[view-window-view]: ../src/main/java/se/ltu/dcc/upcheck/view/WindowView.java

## Controller

Controllers makes the models of the application available via views. Controllers fall into two major categories, upstart controllers and runtime controllers. The former category are located in the `se.ltu.dcc.upckeck` package, while the latter are in the `se.ltu.dcc.upckeck.controller` package. Upstart controllers are concerned with application startup and termination, while the runtime controllers govern behavior at runtime.

Controllers may depend on any classes available anywhere.

Some significant classes, from an architectural standpoint, are [Bootstrap][controller-bootstrap], [Main][controller-main], [Controller][controller-controller] and [Navigator][controller-navigator].

[controller-bootstrap]: ../src/main/java/se/ltu/dcc/upcheck/Bootstrap.java
[controller-main]: ../src/main/java/se/ltu/dcc/upcheck/Main.java
[controller-controller]: ../src/main/java/se/ltu/dcc/upcheck/controller/Controller.java
[controller-navigator]: ../src/main/java/se/ltu/dcc/upcheck/controller/Navigator.java

## Utilities

All classes that don't strictly fall into any of the MVC categories are referred to as utilities. These deal with general concerns, and may be used freely by any part of the application.

A general rule of thumb is that if a class just as well could have been in the Java standard library, its likely a utility.