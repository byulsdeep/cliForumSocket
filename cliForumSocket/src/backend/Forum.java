package backend;

import java.util.StringTokenizer;

import database.DataAccessObject;
import rules.ServiceRules;
import utilities.ProjectUtils;

public class Forum implements ServiceRules {

	public String backController(String clientData, ProjectUtils pu) {
		String message = "false";
		String jobCode;
		if (clientData != null) {
			jobCode = pu.getJobCode(clientData);
			switch (jobCode) {
			case "deletePost":
				message = this.deletePost(clientData, pu);
				break;
			case "getPosts":
				message = this.getPosts(pu);
				break;
			case "addPost":
				message = this.addPost(clientData, pu);
				break;
			case "getNextPostIdx":
				message = this.getNextPostIdx(pu);
				break;
			case "getComments":
				message = this.getComments(clientData, pu);
				break;
			case "getNextCommentIdx":
				message = this.getNextCommentIdx(clientData, pu);
				break;
			case "addComment":
				message = this.addComment(clientData, pu);
				break;
			}
		}
		return message;
	}
	public String getComments(String clientData, ProjectUtils pu) {
		String serverData = "false";
		String[][] exData = pu.extractData(clientData);
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(true, "/src/database/comments.txt", false)) {
			serverData = dao.getComments(exData[0][1]);
		}
		dao.fileClose(true);
		return serverData;
	}
	public String getNextCommentIdx(String clientData, ProjectUtils pu) {
		String[][] exData = pu.extractData(clientData);
		String postIdx = exData[0][1];
		String serverData = "false";
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(true, "/src/database/comments.txt", false)) {
			serverData = String.valueOf(dao.getNextCommentIdx(postIdx, pu));
		}
		dao.fileClose(true);
		return serverData;
	}
	public String addComment(String clientData, ProjectUtils pu) {
		String[][] exData = pu.extractData(clientData);
		String message = "false";
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(false, "/src/database/comments.txt", true)) {
			message = dao.insComment(exData) ? "true" : "false";
		}
		dao.fileClose(false);
		return message;
	}
	public String deletePost(String clientData, ProjectUtils pu) {
		String message = "false";
		String[][] exData = pu.extractData(clientData);
		String postIdx = exData[0][1];
		
		//?????? ??? ????????????
		String allPosts = this.getPosts(pu);
		StringTokenizer st = new StringTokenizer(allPosts, "\n");
		String token;
		StringBuilder sb = new StringBuilder();
		// ????????? ??? ??????
		while (st.hasMoreTokens()) {
			if (!(token = st.nextToken()).substring(0, token.indexOf("|")).equals(postIdx)) {
				sb.append(token + "\n");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		// ?????? ?????? ????????????
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(false, "/src/database/posts.txt", false)) {
			message = dao.insPost(sb.toString()) ? "true" : "false";	
		} else {
			return "false";
		}
		dao.fileClose(false);
		sb.setLength(0);
		// ?????? ?????? ????????????
		if (dao.fileConnected(true, "/src/database/comments.txt", false)) {
			st = new StringTokenizer(dao.getComments(), "\n");
		} else {
			return "false";
		}
		dao.fileClose(true);
		
		// ????????? ?????? ?????? ??????
		dao.fileConnected(true, "/src/database/comments.txt", false);
		dao.fileClose(true);
		
		while (st.hasMoreTokens()) {
			if (!(token = st.nextToken()).substring(0, token.indexOf("|")).equals(postIdx)) {
				sb.append(token + "\n");
			} 
		}
		sb.deleteCharAt(sb.length() - 1);
		// ?????? ?????? ????????????
		if (dao.fileConnected(false, "/src/database/comments.txt", false)) {
			message = dao.insComment(sb.toString()) ? "true" : "false";
		} else {
			return "false";
		}
		dao.fileClose(false);

		return message;
	}
	public String getPosts(ProjectUtils pu) {
		String message = "false";
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(true, "/src/database/posts.txt", false)) {
			message = dao.getPosts(pu);
		}
		dao.fileClose(true);
		return message;
	}
	public String getNextPostIdx(ProjectUtils pu) {
		String message = "false";
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(true, "/src/database/posts.txt", false)) {
			message = String.valueOf(dao.getNextPostIdx(pu));
		}
		dao.fileClose(true);
		return message;
	}
	public String addPost(String clientData, ProjectUtils pu) {
		// addPost??????????=1?????????=admin&??????=1&??????=1
		String message = "false";
		String[][] exData = pu.extractData(clientData);
		DataAccessObject dao = new DataAccessObject();
		if (dao.fileConnected(false, "/src/database/posts.txt", true)) {
			message = dao.insPost(exData) ? "true" : "false";
		}
		dao.fileClose(false);
		return message;
	}
}
