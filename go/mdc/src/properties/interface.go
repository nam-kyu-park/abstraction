package properties

import (
	"config"
	"parser"
	"sync"
)

var m sync.Mutex

func GetService(property *config.ConfigParser) *parser.Service_attr {
	return &property.Config.Service
}

func GetControls(property *config.ConfigParser, name string) *parser.Control_attr {
	for _, sub := range property.Config.Controls {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}

func GetServer(property *parser.Control_attr, name string) *parser.Server_attr {
	for _, sub := range property.Server {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}

func GetPoints(property *parser.PointProperty, name string) *parser.Point_attr {
	for _, sub := range property.Attr {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}

func GetPointEntry(property *parser.Point_attr, name string) *parser.Point_entry_attr {
	for _, sub := range property.Entry {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}

func GetTasks(property *parser.TaskProperty, name string) *parser.Task_attr {
	for _, sub := range property.Attr {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}

func GetTaskEntry(property *parser.Task_attr, name string) *parser.Task_entry_attr {
	for _, sub := range property.Entry {
		if sub.Name == name {
			return &sub
		}
	}
	return nil
}
