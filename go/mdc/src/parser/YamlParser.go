package parser

import (
	"LogManager"
	"fmt"
	"io/ioutil"
	"path/filepath"

	"gopkg.in/yaml.v3"
)

var logger LogManager.Logger

func ToString(value interface{}) string {
	return fmt.Sprintf("%v", value)
}

type YamlObject map[interface{}]interface{}

type YamlParserImp struct {
	err      error
	filename string
	buffer   []byte
}

type YamlParser interface {
	Open(path string) error
	GetBuffer() []byte
	Parse() map[interface{}]interface{}
	GetPath() string
	GetDir() string
}

func GetYamlParser() YamlParser {
	logger = LogManager.GetLogger("YamlParser")
	return new(YamlParserImp)
}

func (m *YamlParserImp) Open(path string) error {
	m.filename, _ = filepath.Abs(path)
	m.buffer, m.err = ioutil.ReadFile(m.filename)
	return m.err
}

func (m *YamlParserImp) GetPath() string {
	return m.filename
}

func (m *YamlParserImp) GetDir() string {
	return filepath.Dir(m.filename)
}

func (m *YamlParserImp) GetBuffer() []byte {
	return m.buffer
}

func (m *YamlParserImp) Parse() map[interface{}]interface{} {
	v := make(map[interface{}]interface{})
	err := yaml.Unmarshal(m.GetBuffer(), &v)
	if err != nil {
		return nil
	}
	return v
}
