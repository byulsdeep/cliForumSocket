package backend;

import database.DataAccessObject;
import rules.ServiceRules;
import utilities.ProjectUtils;

public class Authentication implements ServiceRules {
	public String backController(String clientData, ProjectUtils pu) {
		String message = "server error";
		String jobCode;
		if (clientData != null) {
			jobCode = pu.getJobCode(clientData);		
			switch (jobCode) {
			case "isIdUsed":
				message = isIdUsed(clientData, pu);
				break;
			case "signUp":
				message = signUp(clientData, pu);
				break;
			case "logIn":
				message = logIn(clientData, pu);
			}
		}
		return message;
	}
	private String logIn(String clientData, ProjectUtils pu) {
		String message = "wrong password";
		String[][] exData = pu.extractData(clientData);
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(true, "/src/database/users.txt", false)) {
			message = dao.getUserInfo(exData);
		}
		dao.fileClose(false);
		return message;
	}
	private String isIdUsed(String clientData, ProjectUtils pu) {
		String message = "false";
		String[][] exData = pu.extractData(clientData);
		DataAccessObject dao = new DataAccessObject();
		String[] userIds;
		if (dao.fileConnected(true, "/src/database/users.txt", false)) {
			userIds = dao.getIdList().split(",");
			for (String id : userIds) {
				if (exData[0][1].equals(id)) {
					message = "true";
					break;
				}
			}
		}
		dao.fileClose(true);
		return message;
	}
	private String signUp(String clientData, ProjectUtils pu) {
		//signUp?id=byulsdeep&pw=****&birthday=19940602&recoveryQ=1&recoveryA=yanghwa
		String message = "false";
		String[][] exData = pu.extractData(clientData);
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(false, "/src/database/users.txt", true)) {
			message = dao.insUser(exData) ? "true" : "false";
		}
		dao.fileClose(false);
		return message;
	}
}
