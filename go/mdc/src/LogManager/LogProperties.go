package LogManager

type LogProperties struct {
	path string
}

var logproperties *LogProperties

func GetLogProperties() *LogProperties {
	locker.Lock()
	defer locker.Unlock()
	if logproperties == nil {
		logproperties = &LogProperties{}
	}
	return logproperties
}

func (o *LogProperties) SetPath(path string) {
	o.path = path
}

func (o *LogProperties) GetPath() string {
	return o.path
}
