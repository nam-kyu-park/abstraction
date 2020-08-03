package service

import (
	"LogManager"
	"crypto/rand"
	"fmt"
	"math/big"
	"packet"
	"strings"
	"time"
)

type Secheduler struct {
	logger LogManager.Logger
	sender Sender
	count  int
}

func NewSecheduler() *Secheduler {
	instance := &Secheduler{}
	instance.logger = LogManager.GetLogger("Service")
	instance.count = 0
	return instance
}

func (this *Secheduler) SetSender(s Sender) {
	this.sender = s
}

func (this *Secheduler) Active() {
}

func (this *Secheduler) Recv(p *packet.Packet) {
	go this.fork()
}

func (this *Secheduler) RecvByte(b []byte) {

}

func (this *Secheduler) fork() {
	//this.test()
	this.monitor()
}

func (this *Secheduler) monitor() {
	for {
		time.Sleep(time.Second * 3)

		this.count++
		data := packet.New()
		data.Service = packet.Collector

		// id
		data.Append(packet.DataPoint{Name: "id", Value: "dcim_unit_002"})

		// cpu_status
		data.Append(packet.DataPoint{Name: "cpu_status", Value: ToString(GetRandomInt64(2))})

		// memory
		data.Append(packet.DataPoint{Name: "memory", Value: ToString(GetRandomInt64(100))})

		// fan
		data.Append(packet.DataPoint{Name: "fan", Value: ToString(GetRandomInt64(100))})

		// temp
		data.Append(packet.DataPoint{Name: "temp", Value: ToString(GetRandomInt64(100))})

		// amp
		data.Append(packet.DataPoint{Name: "amp", Value: ToString(GetRandomInt64(100))})

		// volt
		data.Append(packet.DataPoint{Name: "volt", Value: ToString(GetRandomInt64(300))})

		// timestamp
		t := time.Now()
		data.Append(packet.DataPoint{Name: "timestamp", Value: t.Format("20200518144300")})

		var b strings.Builder
		fmt.Fprintf(&b, "[")
		for _, point := range data.Point {
			fmt.Fprintf(&b, "%s=%s, ", point.Name, point.Value)
		}
		fmt.Fprintf(&b, "]")
		this.sender.Write(data.Encode())
		this.logger.Info("Write data (%d): %v", this.count, b.String())
	}
}

func (this *Secheduler) test() {
	// for i := 0; i < 1000; i++ {
	// 	this.logger.Info("secheduler running...")
	// 	data := packet.New()
	// 	data.Service = packet.Collector

	// 	var b strings.Builder
	// 	for i := 0; i < int(GetRandomInt64(5))+1; i++ {
	// 		p1 := packet.DataPoint{Value: ToString(GetRandomInt64(100))}
	// 		data.Append(p1)
	// 		fmt.Fprintf(&b, "(%d: %3d), ", p1.Index, p1.Value)
	// 	}

	// 	this.sender.Write(data.Encode())
	// 	this.logger.Info("Write data (%d): %v", i+1, b.String())
	// }
}

func GetRandomInt64(max int64) int64 {
	n, err := rand.Int(rand.Reader, big.NewInt(max))
	if err != nil {
		panic(err)
	}
	return n.Int64()
}

func ToString(v int64) string {
	return fmt.Sprintf("%d", int(v))
}
