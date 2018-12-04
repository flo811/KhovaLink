# KhovaLink
First published version (2018-12-03).
Author : Florian PALACIN
Mail : flo811@msn.com

Created on Netneans 8.2 using JavaSE 8 and FavaFX for display (needs Maths package aviable on https://github.com/flo811/Maths).

Allows Khovanov homology calculation for knots and links (up to about 13 crossings, depending on aviable RAM).

## Main menu.
On opening you get the main menu.

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/Home.png)

## Create a link.
You can create a link in two ways :

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/DrawWindow.png)

### By typing informations.
Just enter the fields then validate, KhovaLink will first search for inconsistencies.

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/LinkByInfos.png)

### By drawing.
Or you can directly draw the link..

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/Drown.png)

## Save a link.
You can choose to save the link to a database (all links are save to a .link file in the 'LinkDB' directory) or open one from it.

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/Save.png)

## Calculate Khovanov homology.
Once a link is opened you can calculate it's Khovanov homology.
It will set the link informations first the display current status.

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/Calculating.png)

Once calculation is done the result is displayed.

![](https://github.com/flo811/KhovaLink/blob/master/snapshots/Result.png)

You will have a 'Calculation failed' if the link has too many crossings and needs too much memory to calculate.
