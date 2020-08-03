package main

import (
	"LogManager"
	"core"
	"flag"
	"properties"
	"support/rack"
)

func FlagParse() (string, string, bool) {
	cfgPath := flag.String("i", "", "Input the file path for configuring the rack-client.")
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

	config := properties.GetConfigParser()
	config.Load(cfgPath)
	logger.Info("parse config completed. (%s)", cfgPath)
	logger.Info("set file to save log. (%s)", logPath)

	rackParser := rack.GetRackClientParser()
	rackParser.Super = config

	chanal := rack.NewChanal()
	handler := rack.NewHandler(chanal)
	handler.Registry(0, new(rack.ServiceClient))

	client := core.CreateClient()
	client.SetHandler(handler)

	err := client.Connect(rackParser.Host(), rackParser.Port())
	if err != nil {
		logger.Error("connect to server : %s", err.Error())
	}
	client.Join()
	logger.Info("close client")
}
