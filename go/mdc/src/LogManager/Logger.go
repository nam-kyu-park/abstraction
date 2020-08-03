package LogManager

import (
	"fmt"
	"io"
	"log"
	"os"
	"sync"
)

var locker sync.Mutex

type Logger interface {
	Trace(format string, a ...interface{})
	Info(format string, a ...interface{})
	Warn(format string, a ...interface{})
	Error(format string, a ...interface{})
	Debug(format string, a ...interface{})
}

type LoggerImp struct {
	name string
}

var (
	traceLogger *log.Logger
	infoLogger  *log.Logger
	warnLogger  *log.Logger
	errorLooger *log.Logger
	debugLooger *log.Logger
	fileWriter  io.Writer
	maxLen      int
)

func New(file string) *LoggerImp {

	locker.Lock()
	defer locker.Unlock()

	if fileWriter == nil {
		var fp, err = os.OpenFile(file, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0666)
		if err != nil {
			fileWriter = io.MultiWriter(os.Stdout)
		} else {
			fileWriter = io.MultiWriter(fp, os.Stdout)
		}
	}

	var flags int
	flags = log.Ldate | log.Ltime | log.Lmicroseconds

	if traceLogger == nil {
		//traceLogger = log.New(fileWriter, "TRACE: ", flags)
		traceLogger = log.New(fileWriter, "", flags)
	}

	if infoLogger == nil {
		//infoLogger = log.New(fileWriter, "INFO: ", flags)
		infoLogger = log.New(fileWriter, "", flags)
	}

	if warnLogger == nil {
		//warnLogger = log.New(fileWriter, "WARNING: ", flags)
		warnLogger = log.New(fileWriter, "", flags)
	}

	if errorLooger == nil {
		//errorLooger = log.New(fileWriter, "ERROR: ", flags)
		errorLooger = log.New(fileWriter, "", flags)
	}

	if debugLooger == nil {
		//debugLooger = log.New(fileWriter, "DEBUG: ", flags)
		debugLooger = log.New(fileWriter, "", flags)
	}

	return &LoggerImp{}
}

func GetLogger(name string) Logger {
	var path string = GetLogProperties().GetPath()
	current, err := GetLoggerMananger().Get(name)
	if !err {
		imp := New(path)
		imp.name = name
		var newLogger Logger = imp
		GetLoggerMananger().Apend(name, newLogger)
		current = newLogger
	}
	return current
}

func (o *LoggerImp) Trace(format string, a ...interface{}) {
	locker.Lock()
	defer locker.Unlock()
	traceLogger.Printf(o.GetPrefixFormat("TRACE")+o.GetNameFormat()+format, a...)
}

func (o *LoggerImp) Info(format string, a ...interface{}) {
	locker.Lock()
	defer locker.Unlock()
	infoLogger.Printf(o.GetPrefixFormat("INFO")+o.GetNameFormat()+format, a...)
}

func (o *LoggerImp) Warn(format string, a ...interface{}) {
	locker.Lock()
	defer locker.Unlock()
	warnLogger.Printf(o.GetPrefixFormat("WARN")+o.GetNameFormat()+format, a...)
}

func (o *LoggerImp) Error(format string, a ...interface{}) {
	locker.Lock()
	defer locker.Unlock()
	errorLooger.Printf(o.GetPrefixFormat("ERROR")+o.GetNameFormat()+format, a...)
}

func (o *LoggerImp) Debug(format string, a ...interface{}) {
	locker.Lock()
	defer locker.Unlock()
	debugLooger.Printf(o.GetPrefixFormat("DEBUG")+o.GetNameFormat()+format, a...)
}

func (o *LoggerImp) GetNameFormat() string {
	maxLen = Max(maxLen, len(o.name))
	nameFormat := fmt.Sprintf("%%-%ds: ", maxLen)
	return fmt.Sprintf(nameFormat, o.name)
}

func (o *LoggerImp) GetPrefixFormat(s string) string {
	return fmt.Sprintf("%-5s ", s)
}

func Max(x, y int) int {
	if x < y {
		return y
	}
	return x
}
