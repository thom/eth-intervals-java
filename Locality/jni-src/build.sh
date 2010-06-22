#!/bin/bash

cd `dirname $BASH_SOURCE`
gcc -Wall -pthread -shared -fPIC -I/usr/lib/jvm/java-6-sun/include/ -I/usr/lib/jvm/java-6-sun/include/linux/ ch_ethz_hwloc_Affinity.c -o libAffinity.so
mv libAffinity.so ../bin
