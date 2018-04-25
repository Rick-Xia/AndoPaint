     ____.           ___________           __         .__
    |    |          /   _____/  | __ _____/  |_  ____ |  |__
    |    |  ______  \_____  \|  |/ // __ \   __\/ ___\|  |  \
/\__|    | /_____/  /        \    <\  ___/|  | \  \___|   Y  \
\________|         /_______  /__|_ \\___  >__|  \___  >___|  /
                           \/     \/    \/          \/     \/
                                             (Android Edition)
                                     By Richard Xia
Development Environment:
- Built on Windows 8.1
- IDE: Android Studio 2.1 against Nexus 7 (2013); API 23 (Android 6.0/Marshmallow)
- Heap size was not changed, and other settings should be default

Features:
- Selection Tool: allows you to select shapes, selecting the canvas will reset the selection, selecting a shape will set the current colour and thickness to that of the selected shape
- Erase Tool: tap on a shape to delete the shape; tap and hold on the erase tool button to delete all shapes
- Line Drawing Tool: represented by a pencil icon, select it to draw lines
- Rectangle Drawing Tool: represented by a dotted rectangle icon, select it to draw rectangles
- Fill Tool: select a colour then a shape to set the shape fill colour
- Colour Palette: only supports 4 colours, selecting a shape first then a colour will change border colour
- Thickness Palette: supports 3 thickness choices,  selecting a shape first then thickness will change the shape's thickness

Other Notes:
- switching tools will deselect the selected shape
- a selected shape's thickness can be changed by tapping on the thickness button
- shapes can be moved by using the selection tool and then dragging the shape
- a dotted rectangle will appear for a selected shape or as a preview for drawing a shape
- the canvas auto-rotate to match the current screen orientation, on portrait the toolset will be at the top of the screen, while on landscape it will appear on the left
- to prevent clipping the canvas zoom feature will be zoomed in/auto
- model code from A2 was re-used to handle shape drawing and tool selection

Enhancmenets:

- Multi-touch support (support for pinch-to-zoom and panning using ONE finger)