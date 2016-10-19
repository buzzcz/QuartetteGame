cmake_minimum_required(VERSION 3.6)
project(QuartetteServer)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES src/main.cpp src/main.cpp src/player.h src/game.h)
add_executable(QuartetteServer ${SOURCE_FILES})