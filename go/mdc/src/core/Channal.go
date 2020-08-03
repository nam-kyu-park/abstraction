package core

import (
	"LogManager"
	"bufio"
	"bytes"
	"net"
	"packet"
	"sync"
)

type Chanal interface {
	Active()
	Read() ([]byte, int)
	Write(buffer []byte) int
	Close()
	IsRun() bool
	SetConn(conn net.Conn)
}

type ChanalManager struct {
	conn        net.Conn
	instream    chan []byte
	outstream   chan []byte
	logger      LogManager.Logger
	closeRecv   bool
	closeSend   bool
	recvBuffer  []byte
	codecBuffer bytes.Buffer
	locker      sync.Mutex
	readLocker  sync.Mutex
	writeLocker sync.Mutex
}

func NewChanal() Chanal {
	instance := new(ChanalManager)
	instance.instream = make(chan []byte)
	instance.outstream = make(chan []byte)
	instance.logger = LogManager.GetLogger("Chanal")
	instance.recvBuffer = make([]byte, 1024*2)
	instance.codecBuffer.Reset()
	return instance
}

func (this *ChanalManager) SetConn(conn net.Conn) {
	this.conn = conn
}

func (this *ChanalManager) Active() {
	go this.recevier()
	//go this.sender()
}

func (this *ChanalManager) Close() {
	this.logger.Info("close chanal")
	this.conn.Close()
	this.closeRecv = true
	this.closeSend = true
}

func (this *ChanalManager) IsRun() bool {
	return !this.closeRecv && !this.closeSend
}

func (this *ChanalManager) Read() ([]byte, int) {
	this.readLocker.Lock()
	defer this.readLocker.Unlock()
	buffer := <-this.instream
	return buffer, len(buffer)
}

func (this *ChanalManager) Write(buffer []byte) int {
	// this.outstream <- buffer
	// return len(buffer)

	this.writeLocker.Lock()
	defer this.writeLocker.Unlock()
	write(this.conn, packet.Build(buffer))
	return len(buffer)
}

func write(conn net.Conn, content []byte) (int, error) {
	writer := bufio.NewWriter(conn)
	number, err := writer.Write(content)
	if err == nil {
		err = writer.Flush()
	}
	return number, err
}

// func (this *ChanalManager) sender() {
// 	for {
// 		if this.closeSend {
// 			this.logger.Error("close sender.")
// 			return
// 		}

// 		select {
// 		case message, ok := <-this.outstream:
// 			if ok && len(message) > 0 {
// 				buffer := packet.Build(message)
// 				//this.logger.Info("sender: \n%s", packet.Debug(buffer))
// 				write(this.conn, buffer)
// 			}
// 		}
// 	}
// }

func (this *ChanalManager) recevier() {
	for {
		if this.closeRecv {
			this.logger.Error("close recevier.")
			return
		}
		buffer := this.recvMessage()
		//this.logger.Info("recevier: \n%s", packet.Debug(buffer))
		this.instream <- buffer
		//time.Sleep(time.Duration(1))
	}
}

func (this *ChanalManager) recvMessage() []byte {
	this.locker.Lock()
	defer this.locker.Unlock()

	// Read Head
	if err := packet.ReadHeader(this.conn, &this.recvBuffer); err != nil {
		return nil
	}

	// Read Length
	size := packet.ReadLength(this.conn, &this.recvBuffer)
	if size < 1 {
		return nil
	}

	// Read Body
	this.codecBuffer.Reset()
	if n := packet.ReadBody(this.conn, &this.recvBuffer, size, &this.codecBuffer); n != size {
		return nil
	}

	// Convert Body
	data := this.codecBuffer.Bytes()

	// Check Checksum
	cs1 := packet.CheckSum(data)
	cs2 := packet.ReadChecksum(this.conn, &this.recvBuffer)
	if cs1 != cs2 {
		return nil
	}

	// Read tail
	if err := packet.ReadTail(this.conn, &this.recvBuffer); err != nil {
		return nil
	}

	return data
}
