package parser

import (
	"path"
	"path/filepath"

	"gopkg.in/yaml.v3"
)

type ConfigParser interface {
	UnmarshalYaml(parser YamlParser) error
}

// UnmarshalYaml is get service VersionProperty
func (s *VersionProperty) UnmarshalYaml(parser YamlParser) error {
	property := YamlObject{}
	err := yaml.Unmarshal(parser.GetBuffer(), &property)
	if err != nil {
		return err
	}
	s.Attr = Version_attr{Version: ToString(property["version"])}
	return nil
}

// UnmarshalYaml is get service ServiceProperty
func (s *ServiceProperty) UnmarshalYaml(parser YamlParser) error {
	err := yaml.Unmarshal(parser.GetBuffer(), &s)
	if err != nil {
		return err
	}
	return nil
}

// UnmarshalYaml is get service VenderProperty
func (s *VenderProperty) UnmarshalYaml(parser YamlParser) error {
	err := yaml.Unmarshal(parser.GetBuffer(), &s)
	if err != nil {
		return err
	}
	return nil
}

// UnmarshalYaml is get service ControlProperty
func (s *ControlProperty) UnmarshalYaml(parser YamlParser) error {
	err := yaml.Unmarshal(parser.GetBuffer(), &s)
	if err != nil {
		return err
	}

	for i := range s.Attr {
		sub := GetYamlParser()
		file, _ := filepath.Abs(path.Join(parser.GetDir(), s.Attr[i].Include))
		err := sub.Open(file)
		if err != nil {
			return err
		}

		type FindServer struct {
			Server []Server_attr `yaml:"server"`
		}

		find := FindServer{}
		err = yaml.Unmarshal(sub.GetBuffer(), &find)
		if err != nil {
			return err
		}

		s.Attr[i].Server = append(s.Attr[i].Server, find.Server...)
	}

	return nil
}

// UnmarshalYaml is get service ControlProperty
func (s *PointProperty) UnmarshalYaml(parser YamlParser) error {
	err := yaml.Unmarshal(parser.GetBuffer(), &s)
	if err != nil {
		return err
	}

	for i := range s.Attr {
		cfg := GetYamlParser()
		err := cfg.Open(s.Attr[i].Include)
		if err != nil {
			return err
		}

		type FindPoints struct {
			Entry []Point_entry_attr `yaml:"entry"`
		}

		find := FindPoints{}
		err = yaml.Unmarshal(cfg.GetBuffer(), &find)
		if err != nil {
			return err
		}

		s.Attr[i].Entry = append(s.Attr[i].Entry, find.Entry...)
	}

	return nil
}

// UnmarshalYaml is get service ControlProperty
func (s *TaskProperty) UnmarshalYaml(parser YamlParser) error {
	err := yaml.Unmarshal(parser.GetBuffer(), &s)
	if err != nil {
		return err
	}

	for i := range s.Attr {
		cfg := GetYamlParser()
		err := cfg.Open(s.Attr[i].Include)
		if err != nil {
			return err
		}

		type FindTasks struct {
			Entry []Task_entry_attr `yaml:"task-entry"`
		}

		find := FindTasks{}
		err = yaml.Unmarshal(cfg.GetBuffer(), &find)
		if err != nil {
			return err
		}

		s.Attr[i].Entry = append(s.Attr[i].Entry, find.Entry...)
	}

	return nil
}
