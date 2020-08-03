package config

import (
	"parser"
	"sync"
)

type ConfigManager interface {
	Load(path string) error
}

type ConfigParser struct {
	Config parser.Properties
	Path   string
}

var m sync.Mutex
var configParser *ConfigParser

func GetConfigParser() *ConfigParser {
	m.Lock()
	defer m.Unlock()
	if configParser == nil {
		configParser = &ConfigParser{}
	}
	return configParser
}

func (this *ConfigParser) Load(path string) error {
	this.Path = path

	cfg := parser.GetYamlParser()
	err := cfg.Open(this.Path)
	if err != nil {
		return err
	}

	// m := parser.PropertiesEx{}

	// err = yaml.Unmarshal(config.GetBuffer(), &m)
	// if err != nil {
	// 	return err
	// }

	//fmt.Printf("%v", string(cfg.GetBuffer()))

	version := parser.VersionProperty{}
	version.UnmarshalYaml(cfg)

	service := parser.ServiceProperty{}
	service.UnmarshalYaml(cfg)

	vender := parser.VenderProperty{}
	vender.UnmarshalYaml(cfg)

	control := parser.ControlProperty{}
	control.UnmarshalYaml(cfg)

	task := parser.TaskProperty{}
	task.UnmarshalYaml(cfg)

	point := parser.PointProperty{}
	point.UnmarshalYaml(cfg)

	this.Config.Version = version.Attr
	this.Config.Service = service.Attr
	this.Config.Vender = vender.Attr
	this.Config.Controls = append(this.Config.Controls, control.Attr...)
	this.Config.Tasks = append(this.Config.Tasks, task.Attr...)
	this.Config.Points = append(this.Config.Points, point.Attr...)

	return nil
}
