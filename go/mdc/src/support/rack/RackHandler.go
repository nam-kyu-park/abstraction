package rack

import (
	"LogManager"
	"fmt"
	"net"
	"service"
	"sync"
	"time"
)

type Handler interface {
	Registry(id int, sv interface{})
	Activate(id int)
	Run()
	Close()
	Error() error
	SetConn(conn net.Conn)
	IsRun() bool
}

type HandlerManager struct {
	channal     Chanal
	serviceMap  map[int]service.Service
	logger      LogManager.Logger
	bClose      bool
	serviceSize int
}

func NewHandler(channal Chanal, serviceSize int) Handler {
	instance := new(HandlerManager)
	instance.logger = LogManager.GetLogger("Handler")
	instance.channal = channal
	instance.serviceMap = make(map[int]service.Service)
	instance.bClose = false
	instance.serviceSize = serviceSize
	return instance
}

func (this *HandlerManager) Close() {
	this.logger.Info("close handler")
	this.channal.Close()
	this.bClose = true
}

func (this *HandlerManager) Error() error {
	if this.IsRun() {
		return nil
	}
	return fmt.Errorf("Handler close")
}

func (this *HandlerManager) Registry(id int, sv interface{}) {
	this.serviceMap[id] = sv.(service.Service)
	sv.(service.Service).SetSender(this.channal)

}

func (this *HandlerManager) Activate(id int) {
	service := this.serviceMap[id]
	service.Active()
}

func (this *HandlerManager) SetConn(conn net.Conn) {
	this.channal.SetConn(conn)
}

func (this *HandlerManager) IsRun() bool {
	return !this.bClose
}

func (this *HandlerManager) Run() {
	this.channal.Active()
	for _, service := range this.serviceMap {
		service.Active()
	}

	wg := &sync.WaitGroup{}

	wg.Add(this.serviceSize)
	for i := 0; i < this.serviceSize; i++ {
		go this.recv()
	}

	wg.Wait()

	for this.IsRun() {
		time.Sleep(time.Second)
	}
}

// 요청에 대한 처리 수행
func (this *HandlerManager) recv() {
	this.logger.Info("running callback recv function. - rack")
	defer this.Close()
	for {
		buffer, n := this.channal.Read()
		if n < 1 {
			this.logger.Error("close recv: read packet failed.")
			return
		}

		for _, sv := range this.serviceMap {
			if sv == nil {
				this.logger.Warn("select pakcet type service failed.")
				break
			} else {
				go sv.RecvByte(buffer)
			}
		}
	}
}
