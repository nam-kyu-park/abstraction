package rack

import (
	"LogManager"
	"encoding/hex"
	"fmt"
	"packet"
	"service"
)

type RackService struct {
	logger LogManager.Logger
	sender service.Sender
}

func NewRackService() *RackService {
	instance := &RackService{}
	instance.logger = LogManager.GetLogger("Service")
	return instance
}

func (this *RackService) SetSender(s service.Sender) {
	this.sender = s
}

func (this *RackService) Active() {
}

func (this *RackService) Recv(p *packet.Packet) {

}

func (this *RackService) RecvByte(b []byte) {
	this.logger.Info("\n%s", packet.Debug(b))

	data := make([]byte, 1024)
	n, err := hex.Decode(b, data)
	if err != nil {
		this.logger.Info(err.Error())
	}
	fmt.Printf("%s\n", data[:n])
}

func (this *RackService) Send() *packet.Packet {
	return nil
}
