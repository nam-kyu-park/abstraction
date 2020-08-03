package service

import (
	"LogManager"
	"bytes"
	"config"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"packet"
	"properties"
	"strings"
)

type MeasureService struct {
	logger LogManager.Logger
	packet *packet.Packet
	sender Sender
	count  int
	url    string
}

func NewMeasureService() *MeasureService {
	instance := &MeasureService{}
	instance.logger = LogManager.GetLogger("Service")
	instance.count = 0

	parser := config.GetConfigParser()
	control := properties.GetControls(parser, "dcim-adaptation")
	meta := properties.GetServer(control, "adaptation")
	instance.url = fmt.Sprintf("%s://%s:%s", meta.Protocal, meta.IP, meta.Port)

	return instance
}

func (this *MeasureService) SetSender(s Sender) {
	this.sender = s
}

func (this *MeasureService) Active() {

}

func (this *MeasureService) Recv(p *packet.Packet) {
	data := *p

	jsonData := make(map[string]string)

	var b strings.Builder
	fmt.Fprintf(&b, "[")
	for _, point := range data.Point {
		jsonData[point.Name] = point.Value
		fmt.Fprintf(&b, "%s=%s, ", point.Name, point.Value)
	}
	fmt.Fprintf(&b, "]")

	this.count = this.count + 1
	this.logger.Info("Collect data (%d): %v", this.count, b.String())
	this.HandleFunc("/agent-measure", jsonData)
}

func (this *MeasureService) test() {
	// var b strings.Builder
	// for _, d := range p.Point {
	// 	fmt.Fprintf(&b, "(%d: %s), ", d.Index, d.Value)
	// }
	// this.count = this.count + 1
	// this.logger.Info("collect data: %04d -> %v", this.count, b.String())

	// jsonData := map[string]string{"firstname": "Nic", "lastname": "Raboy"}
	// this.HandleFunc("/agent-measure", jsonData)
}

func (this *MeasureService) RecvByte(b []byte) {

}

func (this *MeasureService) GetUrl(api string) string {
	return fmt.Sprintf("%s%s", this.url, api)
}

func (this *MeasureService) HandleFunc(pattern string, jsonData map[string]string) {
	url := this.GetUrl(pattern)
	jsonValue, _ := json.MarshalIndent(jsonData, "", "    ")
	this.logger.Info("request restful api: -> %s\n%s", url, string(jsonValue))

	contentType := "application/json;charset=UTF-8"
	response, err := http.Post(url, contentType, bytes.NewBuffer(jsonValue))
	if err != nil {
		this.logger.Error("The HTTP request failed with error %s\n", err)
	} else {
		data, _ := ioutil.ReadAll(response.Body)
		this.logger.Info(string(data))
	}
}
