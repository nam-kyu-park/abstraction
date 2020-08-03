package service

import (
	"LogManager"
	"packet"
)

type SessionStart struct {
	logger LogManager.Logger
	sender Sender
}

func NewSessionStart() *SessionStart {
	instance := &SessionStart{}
	instance.logger = LogManager.GetLogger("Service")
	return instance
}

func (this *SessionStart) SetSender(s Sender) {
	this.sender = s
}

func (this *SessionStart) Active() {
	this.logger.Info("reauest session join...")
	data := packet.New()
	data.Service = packet.SessionJoin
	this.sender.Write(data.Encode())
}

func (this *SessionStart) Recv(p *packet.Packet) {
	this.logger.Info("session wroking ...")
}

func (this *SessionStart) RecvByte(b []byte) {

}
