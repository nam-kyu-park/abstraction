package service

import (
	"LogManager"
	"packet"
)

type SessionJoin struct {
	logger LogManager.Logger
	sender Sender
}

func NewSessionJoin() *SessionJoin {
	instance := &SessionJoin{}
	instance.logger = LogManager.GetLogger("Service")
	return instance
}

func (this *SessionJoin) SetSender(s Sender) {
	this.sender = s
}

func (this *SessionJoin) Active() {
}

func (this *SessionJoin) Recv(p *packet.Packet) {
	this.logger.Info("comferm session join...")
	data := packet.New()
	data.Service = packet.SessionJoin
	this.sender.Write(data.Encode())
}

func (this *SessionJoin) RecvByte(b []byte) {

}
