cmake_minimum_required(VERSION 3.6)
project(QuartetteServer)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES src/main.cpp src/player.cpp src/player.h src/game.cpp src/game.h src/server.cpp src/server.h src/message.cpp src/message.h src/card.h src/status.h)
add_executable(QuartetteServer ${SOURCE_FILES})