package core

import (
	"LogManager"
	"fmt"
	"net"
	"packet"
	"service"
	"session/providers/memory"
	"time"
)

type ServerImp struct {
	err      error
	listener *net.Listener
	logger   LogManager.Logger
	handler  Handler
}

var serverinstance *ServerImp

func GetServerInstance() *ServerImp {
	m.Lock()
	defer m.Unlock()
	if serverinstance == nil {
		serverinstance = &ServerImp{}
		serverinstance.logger = LogManager.GetLogger("Server")
	}
	return serverinstance
}

func initProvider() {
	memory.InitProvider()
}

func (this *ServerImp) Open(port string) error {
	if port == "" {
		return fmt.Errorf("Server open error: Not input port number. %s", "")
	}

	initProvider()
	this.logger.Info("initailize Provider.")

	listener, err := net.Listen("tcp", ":"+port)
	this.listener = &listener
	this.err = err
	this.logger.Info("open port(%s).", port)
	return this.err
}

func (this *ServerImp) Start() {
	this.logger.Info("server start")
	go this.accept()
}

func (this *ServerImp) Close() {
	(*this.listener).Close()
	this.listener = nil
	this.err = nil
	this.logger = nil
}

// IsRun function is check activate session instance
func (this *ServerImp) IsRun() bool {
	return this.err == nil && this.listener != nil
}

// Join function is handling client operating
func (this *ServerImp) Join() {
	this.logger.Info("join()")
	for this.IsRun() {
		time.Sleep(100 * time.Millisecond)
	}
}

// Run function is free instance
func (this *ServerImp) accept() {
	this.logger.Info("waiting for client connection accept.")
	for {
		if !this.IsRun() {
			this.logger.Error("alive accept.")
			return
		}

		conn, err := (*this.listener).Accept()
		if err != nil {
			this.logger.Error(err.Error())
			this.err = err
			return
		}
		this.logger.Info("client connection accept. %+v", conn.RemoteAddr().(*net.TCPAddr))

		this.handler = NewHandler(NewChanal(), 2)
		this.handler.SetConn(conn)
		this.handler.Registry(int(packet.SessionJoin), service.NewSessionJoin())
		this.handler.Registry(int(packet.Collector), service.NewMeasureService())
		go this.handler.Run()
	}
}

func (this *ServerImp) SetHandler(runner Handler) {
	this.handler = runner
}
