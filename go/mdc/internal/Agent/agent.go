package main

import (
	"LogManager"
	"config"
	"core"
	"flag"
	"properties"
)

func FlagParse() (string, string, bool) {
	cfgPath := flag.String("i", "", "Input the file path for configuring the agent.")
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
	logger.Info("parse config completed. (%s)", cfgPath)
	logger.Info("set file to save log. (%s)", logPath)

	parser := config.GetConfigParser()
	parser.Load(cfgPath)

	control := properties.GetControls(parser, "dcim-collector")
	meta := properties.GetServer(control, "collector")
	client := core.CreateClient()
	err := client.Connect(meta.IP, meta.Port)
	if err != nil {
		logger.Error("connect to server : %s", err.Error())
	}
	client.Join()
	logger.Info("close client")
}
