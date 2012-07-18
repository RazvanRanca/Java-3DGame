#!/bin/sh

if [ ! -d "dependencies" ]; then
  jar xf 3DGame.jar dependencies 
fi

MACHINE_TYPE=`uname -m`
if [ ! ${MACHINE_TYPE} == 'x86_64' ]; then # 32-bit
  mv "dependencies/libj3dcore-ogl.so.32" "dependencies/libj3dcore-ogl.so"
  mv "dependencies/libj3dcore-ogl-cg.so.32" "dependencies/libj3dcore-ogl-cg.so"
fi

if [ ! -d "Models" ]; then
  jar xf 3DGame.jar Models 
fi

if [ ! -d "Sounds" ]; then
  jar xf 3DGame.jar Sounds 
fi

if [ ! -d "Images" ]; then
  jar xf 3DGame.jar Images 
fi

java -jar -Djava.library.path=dependencies 3DGame.jar
