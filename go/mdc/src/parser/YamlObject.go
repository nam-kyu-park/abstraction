package parser

type Version_attr struct {
	Version string `yaml:"version"`
}

type Service_attr struct {
	Name string `yaml:"name"`
	Port string `yaml:"port"`
}

type Vender_attr struct {
	Name    string `yaml:"name"`
	Port    string `yaml:"product"`
	Include string `yaml:"include"`
}

type Server_attr struct {
	Name     string `yaml:"name"`
	Protocal string `yaml:"protocal"`
	IP       string `yaml:"ip"`
	Port     string `yaml:"port"`
	User     string `yaml:"user"`
	Password string `yaml:"password"`
}

type Control_attr struct {
	Name        string        `yaml:"name"`
	DisplayName string        `yaml:"displayName"`
	Include     string        `yaml:"include"`
	Server      []Server_attr `yaml:"server"`
}

type Point_entry_attr struct {
	Name     string `yaml:"name"`
	Value    string `yaml:"value"`
	ValueTye string `yaml:"value-type"`
}

type Point_attr struct {
	Name    string             `yaml:"name"`
	Include string             `yaml:"include"`
	Entry   []Point_entry_attr `yaml:"entry"`
}

type Task_entry_attr struct {
	Name        string `yaml:"name"`
	DisplayName string `yaml:"displayName"`
	Command     string `yaml:"command"`
	Fiter       string `yaml:"fiter"`
	Parse       string `yaml:"parse"`
}

type Task_attr struct {
	Name    string            `yaml:"name"`
	Include string            `yaml:"include"`
	Entry   []Task_entry_attr `yaml:"task-entry"`
}

type Response_type_attr struct {
	ResponseType string `yaml:"response-type"`
}

type VersionProperty struct {
	Attr Version_attr `yaml:"version"`
}

type ServiceProperty struct {
	Attr Service_attr `yaml:"service"`
}

type VenderProperty struct {
	Attr []Vender_attr `yaml:"vender"`
}

type ControlProperty struct {
	Attr []Control_attr `yaml:"controls"`
}

type TaskProperty struct {
	Attr []Task_attr `yaml:"tasks"`
}

type PointProperty struct {
	Attr []Point_attr `yaml:"points"`
}

type ResponseTypeProperty struct {
	Response_type_attr
}

type Properties struct {
	Version      Version_attr
	Service      Service_attr
	Vender       []Vender_attr
	Controls     []Control_attr
	Tasks        []Task_attr
	Points       []Point_attr
	ResponseType Response_type_attr
}

// type PropertiesEx struct {
// 	Version  Version_attr
// 	Service  Service_attr   `yaml:"service"`
// 	Vender   []Vender_attr  `yaml:"vender"`
// 	Controls []Control_attr `yaml:"controls"`
// 	Tasks    []Task_attr    `yaml:"tasks"`
// 	Points   []Point_attr   `yaml:"points"`
// }
