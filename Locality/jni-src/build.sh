#!/bin/bash

gcc -pthread -shared -fPIC -I/usr/lib/jvm/java-6-sun/include/ -I/usr/lib/jvm/java-6-sun/include/linux/ ch_ethz_hwloc_Place.c -o libPlace.so
mv libPlace.so ../bin
