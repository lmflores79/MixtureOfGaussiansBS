# MixtureOfGaussiansBS

Quick implementation in JAVA of the Chris Stauffer and W.E.L Grimson "Adaptive background mixture models for real-time tracking" algorithm described here: http://www.ai.mit.edu/projects/vsam/Publications/stauffer_cvpr98_track.pdf

For image processing, we are making use of [BoofCV](https://boofcv.org/index.php?title=Main_Page).

You can find a Spanish explanation of the algorithm in Spanish in this [Tecnohobby article](http://www.tecnohobby.net/ppal/index.php/vision-computacional/topicos-generales). 

The video file used as example is found at the root directory. The main class to run it is "Main", Please modify the main class to point to the path where the video file can be found at your computer.


When you run the main class, it will display two windows displaying the original video and the foreground one. One will be on top of the other one so please drag the one on top to the one underneath can be watched.
