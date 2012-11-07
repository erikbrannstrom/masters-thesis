#!/bin/sh
javac -classpath ".:weka.jar:colt.jar:miglayout-4.0.jar:sqlite-jdbc-3.7.2.jar" GUI.java
java -Dfile.encoding=utf8 -classpath ".:weka.jar:colt.jar:miglayout-4.0.jar:sqlite-jdbc-3.7.2.jar" GUI