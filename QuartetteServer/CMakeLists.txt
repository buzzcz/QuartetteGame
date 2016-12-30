cmake_minimum_required(VERSION 3.6)
project(QuartetteServer)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES src/Main.cpp src/Player.cpp src/Player.h src/Game.cpp src/Game.h src/Server.cpp src/Server.h src/Message.cpp src/Message.h src/Card.h src/Status.h src/MessageType.h)
add_executable(QuartetteServer ${SOURCE_FILES})