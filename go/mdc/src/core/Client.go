package core

import (
	"LogManager"
	"fmt"
	"net"
	"packet"
	"parser"
	"service"
	"time"
)

type ClientImp struct {
	Path    string
	err     error
	handler Handler
	Version parser.VersionProperty
	Service parser.ServiceProperty
	Control parser.ControlProperty
	Vender  parser.VenderProperty
	logger  LogManager.Logger
}

var clientinstance *ClientImp

func GetClientInstance() *ClientImp {
	m.Lock()
	defer m.Unlock()
	if clientinstance == nil {
		clientinstance = &ClientImp{}
		clientinstance.logger = LogManager.GetLogger("Client")
	}
	return clientinstance
}

func (this *ClientImp) Close() {
	this.handler.Close()
	this.handler = nil
	this.logger = nil
	this.err = nil
}

func (this *ClientImp) Connect(host string, port string) error {
	this.logger.Info("connect to server : [protocal=tcp, host=%s, port=%s]", host, port)
	conn, err := net.Dial("tcp", host+":"+port)
	this.err = err
	if err != nil {
		return fmt.Errorf("connection failed.")
	}

	this.err = conn.(*net.TCPConn).SetKeepAlive(true)
	if this.err != nil {
		return this.err
	}

	this.handler = NewHandler(NewChanal(), 2)
	this.handler.SetConn(conn)
	this.handler.Registry(int(packet.SessionStart), service.NewSessionStart())
	this.handler.Registry(int(packet.SessionJoin), service.NewSecheduler())
	go this.handler.Run()

	return err
}

// IsConnect function is check activate session instance
func (this *ClientImp) IsConnect() bool {
	return this.err == nil && this.handler != nil && this.handler.Error() == nil
}

// Join function is handling client operating
func (this *ClientImp) Join() {
	this.logger.Info("join()")
	for this.IsConnect() {
		time.Sleep(100 * time.Millisecond)
	}
}

func (this *ClientImp) SetHandler(runner Handler) {
	this.handler = runner
}
