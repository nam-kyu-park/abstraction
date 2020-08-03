package core

import (
	"session/providers/memory"
	"sync"
)

var m sync.Mutex

type Server interface {
	Open(port string) error
	Start()
	Join()
	Close()
	SetHandler(runner Handler)
}

type Client interface {
	Connect(host string, port string) error
	Join()
	Close()
	SetHandler(runner Handler)
}

func CreateServer() Server {
	return GetServerInstance()
}

func CreateClient() Client {
	return GetClientInstance()
}

func Initailize() {
	memory.InitProvider()
}
