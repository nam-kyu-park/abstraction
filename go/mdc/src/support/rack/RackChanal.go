package rack

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

func (this *ChanalManager) recevier() {
	for {
		if this.closeRecv {
			this.logger.Error("close recevier.")
			return
		}
		buffer := this.recvMessage()
		this.instream <- buffer
	}
}

func (this *ChanalManager) recvMessage() []byte {
	n, err := this.conn.Read(this.recvBuffer)
	if err == nil {
		//this.logger.Info("recevier: \n%s", packet.Debug(instance.recvBuffer[:n]))
		return this.recvBuffer[:n]
	}
	return nil
}
