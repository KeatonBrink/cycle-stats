package main

import (
	"flag"
	"fmt"
)

var (
	port   = flag.Int("port", 50051, "The server port")
	wsport = flag.Int("wsport", 8081, "The websocket port")
)

type server struct {

}

func main() {
	flag.Parse()
	// Start the gRPC server
	go startGRPCServer(*port)
	fmt.Printf("Server started on port %d\n", *port)
	// Start the websocket server
	startWebsocketServer(*wsport)
}