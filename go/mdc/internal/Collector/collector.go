package main

import (
	"LogManager"
	"config"
	"core"
	"flag"
	"properties"
)

func FlagParse() (string, string, bool) {
	cfgPath := flag.String("i", "", "Input the file path for configuring the collector.")
	logPath := flag.String("l", "./application.log", "Input of file path to save log. (defualt: ./application.log)")
	verbose := flag.Bool("verbose", false, "output debug log")
	flag.Parse()
	return *cfgPath, *logPath, *verbose
}

func main() {

	cfgPath, logPath, verbose := FlagParse()

	LogManager.GetLogProperties().SetPath(logPath)
	var logger = LogManager.GetLogger("Main")
	logger.Info("inventory-path=%s, log-path=%s, verbose=%v", cfgPath, logPath, verbose)
	logger.Info("parse config completed. [path=%s]", cfgPath)
	logger.Info("set path of log file. [path=%s]", logPath)

	parser := config.GetConfigParser()
	parser.Load(cfgPath)

	meta := properties.GetService(parser)
	logger.Info("service port. (%s)", meta.Port)

	server := core.CreateServer()
	err := server.Open(meta.Port)
	if err != nil {
		logger.Error("server open result: failed.")
	}

	server.Start()
	server.Join()
	logger.Info("close server")
}
