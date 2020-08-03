package kr.co.sptek.abstraction.common;

public interface DcimTopicId {
    String TOPIC_ORCHESTRATION = "tpOrchestration";
    String TOPIC_ALARM = "tpAlarm";
    String TOPIC_REMOTE = "tpRemote";
    String TOPIC_DEVICE_HANDLER = "tpDeviceHandler";
    String TOPIC_MONITORING_HANDLER = "tpMonitoringHandler";
    String TOPIC_DIAGNOSIS = "tpDiagnosis";
    String TOPIC_HEALING = "tpHealing";
    String TOPIC_VM = "tpVm";
    String TOPIC_VDEVICE_HANDLER = "tpVdeviceHandler";

    // New Topic
    String TOPIC_DEPLOY = "tpDeploy";
    String TOPIC_DEPLOYSTATUS = "tpDeployStatus";
    String TOPIC_AUTHORIZATION = "tpAuthorization";
    String TOPIC_SYSTEM_ERROR = "tpSystemError";
    String TOPIC_RELOCATION = "tpRelocation";
    String TOPIC_DOCKER = "tpDocker";
    String TOPIC_MONITORING = "tpMonitoring";
    String TOPIC_RESTART = "tpRestart";
}
