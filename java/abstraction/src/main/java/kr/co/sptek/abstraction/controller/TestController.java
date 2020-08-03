package kr.co.sptek.abstraction.controller;


import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import kr.co.sptek.abstraction.common.DcimTopicId;
import kr.co.sptek.abstraction.kafka.topic.DeployTopic;
import kr.co.sptek.abstraction.properties.TestReposProperties;
import kr.co.sptek.abstraction.service.handler.DeployHandler;
import kr.co.sptek.abstraction.service.request.KafkaRequest;
import kr.co.sptek.abstraction.service.response.KafkaResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

	private static final Logger logger = LogManager.getLogger(TestController.class);

	@Autowired
	KafkaRequest kafkaRequest;

	@Autowired
	KafkaResponse kafkaResponse;

	String message;

	@GetMapping(value="/test")
	public String test() throws IOException {

		String topic = DcimTopicId.TOPIC_DIAGNOSIS;
		String data = "{\n" +
				"    \"dcim-locate-001\": {\n" +
				"        \"dcim-unit-001\": {\n" +
				"            \"dcim-point-001\": {\n" +
				"                \"value\": \"0.123\"\n" +
				"            },\n" +
				"            \"dcim_point-002\": {\n" +
				"                \"value\": \"1.23\"\n" +
				"            },\n" +
				"            \"dcim_point-003\": {\n" +
				"                \"value\": \"12\"\n" +
				"            },\n" +
				"            \"timestamp\": \"20200518144300\"\n" +
				"        },\n" +
				"        \"middle-process\": [\n" +
				"            {\n" +
				"                \"code\": \"middle-process-001\",\n" +
				"                \"display\": \"internal adapter to kafka\",\n" +
				"                \"timestamp\": \"20200518144300\"\n" +
				"            }, {\n" +
				"                \"code\": \"middle-process-002\",\n" +
				"                \"display\": \"monitoring to device value\",\n" +
				"                \"timestamp\": \"20200518144300\"\n" +
				"            }\n" +
				"        ]\n" +
				"    }\n" +
				"}\n";

		try {
			kafkaRequest.sendMessage(topic, data);
			return "completed";
		}catch (Exception e)
		{
			logger.info(e.toString());
		}
		return "failed";
	}

	@GetMapping(value="/testFtp")
	public void testFtp() throws IOException, JSchException, SftpException, URISyntaxException {
		//String f1 ="nas/agent/dcim-unit-001/dcim-point-001/ver/1.0/{8e1b5d41-dfa9-479e-8ffd-745d7edba27c}.exe";
		//ftpHandler.download(f1);
		//String f2 ="nas/agent/dcim-unit-001/dcim-point-001/ver/1.0/{8e1b5d41-dfa9-479e-8ffd-745d7edba27d}.exe";
		//ftpHandler.download(f2);
		//String f3 ="D:/tmp/repository/{8e1b5d41-dfa9-479e-8ffd-745d7edba27e}.exe";
		//String to ="repos/nas/agent/dcim-unit-001/dcim-point-001/ver/1.0";
		//ftpHandler.upload(f3, to);

		// get multiple resources files to compress
		File resource1 = new File("d:/tmp/test/in/resource1.txt");
		File resource2 = new File("d:/tmp/test/in/resource2.txt");
		File resource3 = new File("d:/tmp/test/in/resource3.txt");

		// compress multiple resources
		//Zipper.compress("d:/tmp/test/out/example.7z", resource1, resource2, resource3);

		// decompress multiple resources
		//Zipper.decompress("d:/tmp/test/out/example.7z", new File("d:/tmp/test/out/example"));



		// get directory file to compress
		//File directory = new File(clazz.getResource("/in/dir").getFile());
		File[] directory = new File("d:/tmp/test/in").listFiles();

		// compress recursive directory
		//Zipper.compress("d:/tmp/test/out/example2.7z", directory);

		// decompress recursive directory
		//Zipper.decompress("d:/tmp/test/out/example2.7z", new File("d:/tmp/test/out/example2"));

	}

	@GetMapping(value="/testReturn")
	public String testReturn() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_DIAGNOSIS);
		} catch (Exception e) {
			logger.warn("//testReturn API: Response kafka receive message failed");
		}
		return "failed";
	}

	@ResponseBody
	@RequestMapping(value = "/agent-deploy-completed", method = RequestMethod.POST)
	public void AgentDeployCompleted(@RequestBody String topic) throws IOException {

		// JSONObject topic = DeployTopic.testJSON();
		DeployTopic data = DeployTopic.deserializeJSON(topic);
		logger.info(data.toString());

	}

	@PostMapping(value="/heal/agent-deploy-completed-ex")
	public void HealingReturnCallbackProc(@RequestParam(value="company", required=true) String company
			, @RequestParam(value="device", required=true) String device
			, @RequestParam(value="mdc", required=true) String mdc
			, @RequestParam(value="version", required=true) String version
			, @RequestParam(value= "status" , required = true) String status
			, @RequestParam(value= "timestamp" , required = true) String timestamp
	) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("company", company);
		map.put("device", device);
		map.put("mdc", mdc);
		map.put("version", version);
		map.put("status", status);
		map.put("timestamp", timestamp);
	}



//---

	@Autowired
	TestReposProperties testReposProperties;

	public String loadJSON(final String path) throws IOException {
		logger.info("call loadJSON: TestReposProperties [" + testReposProperties.toString() + "]");
		try {
			String file = testReposProperties.getJsonRoot()+ "/" + path;
			logger.info("call loadJSON: path=" + file  + "]");
			JSONParser parser = new JSONParser();
			Object json = parser.parse(new FileReader(file));
			JSONObject obj = (JSONObject)json;
			return obj.toString();
		} catch (Exception e) {
			logger.warn("not find json file: " + path);
		}
		return null;
	}

	@GetMapping(value="/test-agent-send-monitoring")
	public String test1() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_monitor.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_MONITORING_HANDLER, json);
		} catch (Exception e) {
			logger.warn("/test-agent-send-monitoring API: Request kafka send message failed");
		}

		return json;
	}

	@GetMapping(value="/test-agent-recevie-monitoring")
	public String test21() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_MONITORING_HANDLER);
		} catch (Exception e) {
			logger.warn("/test-agent-recevie-monitoring API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-agent-receive-deploy")
	public String test2() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_deploy_status.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_DEPLOYSTATUS, json);
		} catch (Exception e) {
			logger.warn("/test-agent-receive-deploy API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-agent-send-deploy")
	public String test3() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_deploy.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_DEPLOY, json);
		} catch (Exception e) {
			logger.warn("/test-agent-send-deploy API: Request kafka send message failed");
		}
		return json;
	}

	@Autowired
	DeployHandler handler;

	@GetMapping(value="/test-agent-send-authorization-code")
	public String test4() throws IOException {
//		String json = "completed";
//		try {
//			json = loadJSON("dcim_authorization.json");
//			//kafkaRequest.sendMessage(DcimTopicId.TOPIC_AUTHORIZATION, json);
//			String message = json;
//			AuthorizationTopic topic = AuthorizationTopic.deserializeJSON(message);
//
//			try {
//				if (topic != null) {
//					boolean authorization = handler.authorization(topic);
//					topic.status = String.valueOf(authorization);
//
//                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//                    topic.timestamp = timeStamp;
//					//logger.info("topic.timestamp = " + topic.timestamp);
//				}
//			}
//			catch (Exception e) {
//				logger.warn("TOPIC_AUTHORIZATION: Response kafka send outbound message failed.");
//			}
//
//			return topic.serializeJSON();
//
//		} catch (Exception e) {
//			logger.warn("/test-agent-send-authorization-code API: Request kafka send message failed");
//		}
//		return json;
        return "true";
	}

	@GetMapping(value="/test-agent-send-authorization-failed")
	public String test5() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_authorization.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_AUTHORIZATION, json);
		} catch (Exception e) {
			logger.warn("/test-agent-send-authorization-failed API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-agent-receive-authorization")
	public String test6() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_AUTHORIZATION);
		} catch (Exception e) {
			logger.warn("/test-agent-receive-authorization API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-agent-send-system-error")
	public String test7() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_system_error.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_SYSTEM_ERROR, json);
		} catch (Exception e) {
			logger.warn("/test-agent-send-system-error API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-agent-receive-system-error")
	public String test22() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_SYSTEM_ERROR);
		} catch (Exception e) {
			logger.warn("/test-agent-receive-system-error API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-agent-send-alarm")
	public String test8() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_alarm.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_ALARM, json);
		} catch (Exception e) {
			logger.warn("/test-agent-send-alarm API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-agent-receive-alarm")
	public String test23() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_ALARM);
		} catch (Exception e) {
			logger.warn("/test-agent-receive-alarm API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-receive-remote-info")
	public String test9() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_REMOTE);
		} catch (Exception e) {
			logger.warn("/test-receive-remote-info API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-receive-remote")
	public String test10() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_REMOTE);
		} catch (Exception e) {
			logger.warn("/test-receive-remote API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-send-remote-result")
	public String test11() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_diag_heal.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_REMOTE, json);
		} catch (Exception e) {
			logger.warn("/test-send-remote-result API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-receive-recovery")
	public String test12() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_HEALING);
		} catch (Exception e) {
			logger.warn("/test-receive-recovery API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-receive-recovery-info")
	public String test13() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_HEALING);
		} catch (Exception e) {
			logger.warn("/test-receive-recovery-info API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-send-recovery-result")
	public String test14() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_diag_heal.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_HEALING, json);
		} catch (Exception e) {
			logger.warn("/test-send-recovery-result API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-receive-pod-relocation")
	public String test15() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_RELOCATION);
		} catch (Exception e) {
			logger.warn("/test-receive-pod-relocation API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-send-pod-relocation-result")
	public String test16() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_relocation.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_RELOCATION, json);
		} catch (Exception e) {
			logger.warn("/test-send-pod-relocation-result API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-receive-docker-backup-image")
	public String test17() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_DOCKER);
		} catch (Exception e) {
			logger.warn("/test-receive-docker-backup-image API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-send-docker-backup-image-result")
	public String test18() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_docker.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_DOCKER, json);
		} catch (Exception e) {
			logger.warn("/test-send-docker-backup-image-result API: Request kafka send message failed");
		}
		return json;
	}

	@GetMapping(value="/test-recevie-shutdown-pod")
	public String test19() throws IOException {
		try {
			return kafkaResponse.receiveMessage(DcimTopicId.TOPIC_RELOCATION);
		} catch (Exception e) {
			logger.warn("/test-recevie-shutdown-pod API: Request kafka send message failed");
		}
		return "completed";
	}

	@GetMapping(value="/test-send-shutdown-pod-result")
	public String test20() throws IOException {
		String json = "completed";
		try {
			json = loadJSON("dcim_relocation.json");
			kafkaRequest.sendMessage(DcimTopicId.TOPIC_RELOCATION, json);
		} catch (Exception e) {
			logger.warn("/test-send-shutdown-pod-result API: Request kafka send message failed");
		}
		return json;
	}
}
