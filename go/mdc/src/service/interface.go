package service

import (
	"packet"
)

type Sender interface {
	Write(buffer []byte) int
}

type Service interface {
	Active()
	Recv(p *packet.Packet)
	RecvByte(b []byte)
	SetSender(s Sender)
}
