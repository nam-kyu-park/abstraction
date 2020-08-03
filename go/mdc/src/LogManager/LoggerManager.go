package LogManager

type LoggerMananger struct {
	loggerMap map[string]Logger
}

var loggermananger *LoggerMananger

func GetLoggerMananger() *LoggerMananger {
	locker.Lock()
	defer locker.Unlock()
	if loggermananger == nil {
		loggermananger = &LoggerMananger{loggerMap: make(map[string]Logger)}
	}
	return loggermananger
}

func (o *LoggerMananger) Apend(s string, l Logger) {
	o.loggerMap[s] = l
}

func (o *LoggerMananger) Get(s string) (Logger, bool) {
	logger, err := o.loggerMap[s]
	return logger, err
}

func (o *LoggerMananger) clear() {
	o.loggerMap = nil
}

func (o *LoggerMananger) delete(s string) {
	delete(o.loggerMap, s)
}
