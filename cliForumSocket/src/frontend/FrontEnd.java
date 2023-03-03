package frontend;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import beans.PostBean;
import controller.Controller;
import utilities.ProjectUtils;

public class FrontEnd {
	Socket socket;
	BufferedReader inputReader;
	PrintWriter requestSender;
	//BufferedReader responseReader;
	DataInputStream responseReader;
	ProjectUtils pu;
	String userInfo = null;
	List<PostBean> posts = null;

	public FrontEnd() {
		connectToServer();
		pu = new ProjectUtils();
		main();
	}
	public void connectToServer() {
		try {
			socket = new Socket("localhost", 2400);
			System.out.println("Connected to Server.");

			inputReader = new BufferedReader(new InputStreamReader(System.in));
			requestSender = new PrintWriter(socket.getOutputStream(), true);
			//responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			responseReader = new DataInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	void main() {
		String[] options = { "게시판  ", "로그인  ", "회원가입 " };
		String[] options2 = { "게시판  ", "로그아웃  ", "마이페이지" };
		String select;
		try {
			while (true) {
				System.out.println(pu.getTitle());

				if (userInfo != null) {
					System.out.println(pu.getMenu(options2, true));
				} else {
					System.out.println(pu.getMenu(options, true));
				}
				select = inputReader.readLine();
				switch (select) {
				case "1":
					moveForum();
					break;
				case "2":
					if (userInfo == null) {
						moveLogIn();
					} else {
						moveLogOut();
					}
					break;
				case "3":
					if (userInfo == null) {
						moveSignUp();
					} else {
						moveMyPage();
					}
					break;
				case "0":
					System.out.println("종료");
					System.exit(0);
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	private void moveMyPage() {
		String[] options = { "홈페이지 " };
		System.out.println(pu.getTitle("마이페이지", false));
		String select;
		boolean run = true;
		while (run) {
			System.out.println("내 아이디: " + userInfo);
			System.out.println("==================================");
			System.out.println(pu.getMenu(options, true));
			select = getInput();
			switch (select) {
			case "1":
				run = false;
				break;
			case "0":
				System.out.println("종료");
				System.exit(0);
				break;
			}
		}
	}
	private void moveLogOut() {

	}
	private void moveSignUp() {
		System.out.println(pu.getTitle("회원가입", true));
		String[] names = { "id", "pw", "birthday", "recoveryQ", "recoveryA" };
		String[] data = new String[5];
		String clientData;
		String serverData;
		boolean isExit = false;
		while (!isExit) {
			System.out.println("아이디: ");
			data[0] = getInput();
			if (isExit = pu.exitCheck(data[0]))
				break;
			if (pu.containsComma(data[0]))
				continue;
//			if (pu.isEmpty(data[0]))
//				continue;
			// 파일 쓰기 기능 완성하면 중복체크 기능 추가
			clientData = pu.makeTransferData("isIdUsed", names[0], data[0]);
			sendRequest(clientData);
			serverData = getResponse();
			if (serverData.equals("false")) {
				System.out.println("사용 가능한 아이디입니다.");
				pu.confirm();
				break;
			} else {
				System.out.println("이미 사용중인 아이디입니다.");
			}
		}

		isExit = pu.confirmInput(isExit, inputReader);

		while (!isExit) {
			System.out.println("비밀번호: ");
			data[1] = getInput();
			if (isExit = pu.exitCheck(data[1]))
				break;
			if (pu.containsComma(data[1]))
				continue;
			System.out.println("비밀번호 확인: ");
			String confirm = getInput();
			if (isExit = pu.exitCheck(confirm))
				break;
			if (data[1].equals(confirm)) {
				break;
			} else {
				System.out.println("비밀번호가 다릅니다.");
			}
		}

		while (!isExit) {
			System.out.println("생년월일(19910101):  ");
			data[2] = getInput();
			if (isExit = pu.exitCheck(data[2]))
				break;
			if (pu.containsComma(data[2]))
				continue;
			if (!pu.isNum(data[2])) {
				System.out.println("숫자를 입력해주세요.");
				continue;
			}
			if (data[2].length() != 8) {
				System.out.println("19910101 형식으로 8자리 입력해주세요.");
				continue;
			}
			try {
				LocalDate.parse(data[2], DateTimeFormatter.ofPattern("yyyyMMdd"));
				break;
			} catch (Exception e) {
				System.out.println("정상적인 날짜를 입력해주세요.");
			}
		}
		String[] recoveryQ = { "내가 다닌 초등학교는?", "내 이모의 이름은?", "내가 가장 아끼던 장난감은?", "내가 가장 좋아하는 노래는?" };
		if (!isExit) {
			System.out.println("계정 복구용 질문을 선택해주세요: ");
			System.out.println(pu.getMenu(recoveryQ, false));
		}
		int recoveryQIdx = -1;
		while (!isExit) {
			try {
				recoveryQIdx = Integer.parseInt(getInput());
				if (isExit = pu.exitCheck(recoveryQIdx))
					break;
				if (recoveryQIdx < 1 || recoveryQIdx > 4)
					continue;
				break;
			} catch (Exception e) {
				continue;
			}
		}
		if (!isExit) {
			data[3] = String.valueOf(recoveryQIdx);
			System.out.println(recoveryQ[recoveryQIdx - 1]);
		}

		while (!isExit) {
			data[4] = getInput();
			if (pu.containsComma(data[4]))
				continue;
			break;
		}

		if (!(isExit = pu.exitCheck(data[4]))) {
			StringBuffer sb = new StringBuffer();
			sb.append("아이디: " + data[0] + "\n");
			sb.append("비밀번호: ");
			for (int i = 0; i < data[1].length(); i++) {
				sb.append("*");
			}
			sb.append("\n");
			sb.append("생년월일: " + data[2] + "\n");
			sb.append(recoveryQ[recoveryQIdx - 1] + "\n");
			sb.append(data[4] + "\n");
			sb.append("정보를 확인해주세요.\n");
			sb.append("전송 하기");
			System.out.println(sb);
			pu.confirm();
		}

		if (!(isExit = pu.confirmInput(isExit, inputReader))) {
			clientData = pu.makeTransferData("signUp", names, data);
			sendRequest(clientData);
			serverData = getResponse();
			if (serverData.equals("true")) {
				System.out.println(pu.getTitle("가입 성공", false));
				System.out.println("환영합니다, " + data[0] + "님!");
				System.out.println("지금 로그인하시고 새 글을 작성해보세요!");
			}
		}
	}
	private void moveLogIn() {
		String[] names = { "id", "pw" };
		String[] data = new String[2];
		String clientData;
		String serverData = null;
		boolean isExit = false;

		System.out.println(pu.getTitle("로그인", true));
		while (!isExit) {
			System.out.println("아이디 : ");
			data[0] = getInput();
			if (isExit = pu.exitCheck(data[0]))
				break;
			System.out.println("비밀번호: ");
			data[1] = getInput();
			pu.confirm();
			if (!(isExit = pu.confirmInput(isExit, inputReader))) {
				clientData = pu.makeTransferData("logIn", names, data);
				System.out.println(clientData);
				sendRequest(clientData);
				serverData = getResponse();
				if (serverData != null) {
					userInfo = serverData;
					System.out.println(pu.getTitle());
					System.out.println(pu.getAccessInfo(serverData));
					break;
				} else {
					System.out.println("인증 정보가 잘못되었습니다.");
				}
			}
		}
	}
	private void moveForum() {
		// new Controller().entrance("moveForum", pu, sc);
		// new Controller().entrance("moveForum?id=" + userInfo, pu, sc);
		String[] options = { "글쓰기  ", "마이페이지  ", "홈페이지 " };
		String[] options2 = { "홈페이지 " };
		String[] options3 = { "글삭제    ", "돌아가기 " };
		String[] options4 = { "돌아가기 " };
		boolean showAccess = true;
		
		System.out.println(pu.getTitle("게시판", false));
		String select;
		boolean run = true;
		
		while (run) {
			System.out.println(getPosts());
			if (userInfo != null) {
				System.out.println(pu.getMenu(options, true));
			} else {
				System.out.println(pu.getMenu(options2, true));
			}
			select = getInput();
			switch (select) {
			case "1":
				if (userInfo != null) {
					addPost(showAccess);
				} else {
					run = false;
				}
				break;
			case "2":
				moveMyPage();
				break;
			case "3":
				if (userInfo != null)
					run = false;
				break;
			case "0":
				System.out.println("종료");
				System.exit(0);
				break;
			default:
				showPostDetail(posts, select, options3, options4);
			}
		}
	}
	void showPostDetail(List<PostBean> posts, String idx, String[] options3, String[] options4) {
		PostBean po = posts.get(Integer.parseInt(idx) - 4);
		StringBuilder sb = new StringBuilder();
		sb.append("==================================\n");
		sb.append(po.getTitle() + "\n");
		sb.append(po.getUser() + " | " + po.getDate() + " " + po.getTime() + "\n");
		sb.append("----------------------------------\n");
		sb.append(po.getContent());
		sb.append("==================================");
		System.out.println(sb.toString());	
		String select;
		boolean run = true;
		while (run) {
			sb.setLength(0);
			sb.append(pu.getMenu((userInfo != null) ? options3 : options4, true));
			sb.append("\n==================================");
			System.out.println(sb);
			select = getInput();

			switch (select) {
			case "1":
				if (userInfo != null) {
					deletePost(po.getIndex());
					break;
				} else {
					run = false;					
				}
				break;
			case "2":
				if (userInfo != null) run = false;
				break;
			case "0":
				System.out.println("종료");
				System.exit(0);
				break;
			}
		}
	}
	void deletePost(int idx) {
		String serverData = null;
		System.out.println("정말 삭제하시겠습니까?");
		pu.confirm();
		boolean isExit = false;
		if (!(isExit = pu.confirmInput(isExit, inputReader))) {
			sendRequest("deletePost?postIdx=" + idx);
			serverData = getResponse();
		}
		System.out.println(serverData != null ? "게시글이 삭제되었습니다." : "오류");
	}
	String getPosts() {
		String serverData = null;
		sendRequest("getPosts");
		serverData = getResponse();

		StringTokenizer st;
		posts = new ArrayList<>();
		PostBean p;
		String content;
		st = new StringTokenizer(serverData, "|\n");
		while (st.hasMoreTokens()) {
			p = new PostBean();
			p.setIndex(Integer.parseInt(st.nextToken()));
			p.setUser(st.nextToken());
			p.setTitle(st.nextToken());
			content = st.nextToken();
			p.setContent(content.replace("<newLine>", "\n"));
			p.setDate(st.nextToken());
			p.setTime(st.nextToken());
			posts.add(p);
		}

		StringBuffer sb = new StringBuffer();
		//0. 1. 번은 홈페이지, 종료 기능이므로 2번 부터 게시글 선택
		//2. 아름다운 구속		                    		댓글수
		//틀니 | 13:15/2023.03.02 | 조회 99 | 추천 99
		for (int i = 0; i < posts.size(); i++) {   
			sb.append(i + 4 + ". ");
			sb.append(posts.get(i).getTitle() + "\n");
			sb.append(posts.get(i).getUser() + "\t");
			sb.append((pu.getDate().equals(posts.get(i).getDate()) ? posts.get(i).getTime() : posts.get(i).getDate().substring(5,10)));
			//sb.append("조회 "); 조회수, 추천수, 댓글수는 여유되면 추가
			sb.append((i < posts.size() - 1) ? "\n----------------------------------\n" : "\n==================================");
		}
		return sb.toString();
	}

	void addPost(boolean showAccess) {
		String clientData;
		String serverData;
		String[] names = { "번호", "작성자", "제목", "내용", "작성날짜", "작성시간" };
		String[] data = new String[6];

		// 최신글 번호 불러오는 코드 추가
		Controller ctl;
		sendRequest("getMaxPostIdx");
		serverData = getResponse();
		data[0] = serverData;
		
		data[1] = userInfo;
		data[2] = "";
		System.out.print("제목 : ");
		data[2] = getInput();
		System.out.println("내용 (1. 입력 종료): ");
		data[3] = pu.nextPara(inputReader);
		
		pu.confirm();
		boolean isExit = false;

		if (!(isExit = pu.confirmInput(isExit, inputReader))) {
			data[4] = pu.getDate();
			data[5] = pu.getTime();
			clientData = pu.makeTransferData("addPost", names, data);
			sendRequest(clientData);
			serverData = getResponse();
			if (serverData.equals("true")) {
				System.out.println("글이 정상적으로 등록되었습니다.");
				System.out.println("==================================");
				showAccess = false;
			}
		}
	}
	String getInput() {
		try {
			return inputReader.readLine();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	void sendRequest(String clientData) {
		requestSender.println(clientData);
		//print won't work, flush doesn't help
	}
	String getResponse() {
		try {
			//return responseReader.readLine();
			return responseReader.readUTF();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
