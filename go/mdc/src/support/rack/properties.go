package rack

import (
	"sync"
)

var m sync.Mutex

// type RackClientProperties interface {
// 	Host() string
// 	Port() string
// }

// type RackServerProperties interface {
// 	Port() string
// }

// type RackServerParser struct {
// 	super *properties.ConfigParser
// }

// func (o *RackServerParser) Name() string {
// 	return o.super.Service.Attr.Name
// }

// func (o *RackServerParser) Port() string {
// 	return o.super.Service.Attr.Port
// }

// type RackClientParser struct {
// 	Super *properties.ConfigParser
// }

// var clientparser *RackClientParser

// func GetRackClientParser() *RackClientParser {
// 	m.Lock()
// 	defer m.Unlock()
// 	if clientparser == nil {
// 		clientparser = &RackClientParser{}
// 	}
// 	return clientparser
// }

// func (this *RackClientParser) Host() string {
// 	info := this.getControlProperty("rack")
// 	return info.Server.IP
// }

// func (this *RackClientParser) Port() string {
// 	info := this.getControlProperty("rack")
// 	return info.Server.Port
// }

// func (this *RackClientParser) getControlProperty(name string) *parser.Control_attr {
// 	for i := 0; i < len(this.Super.Control.Attr); i++ {
// 		if attr := this.Super.Control.Attr[i]; attr.Name == name {
// 			return &attr
// 		}
// 	}
// 	return nil
// }
