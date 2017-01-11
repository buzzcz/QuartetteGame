CXX = g++ 
BIN = QuartetteServer
OBJ = src/Main.o src/Server.o src/Game.o src/Player.o src/Message.o src/Statistics.o
CXXFLAGS = -std=c++11 -pthread -luuid -Wall

%.o:    %.cpp
	        $(CXX) -c $< -o $@ $(CXXFLAGS)

$(BIN): $(OBJ)
	        ${CXX} $^ -o $@ $(CXXFLAGS)
		        rm -f $(OBJ)

clean: 
	        rm -f $(BIN)
