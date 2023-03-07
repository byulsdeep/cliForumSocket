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
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import beans.CommentBean;
import beans.PostBean;
import utilities.ProjectUtils;

public class FrontEnd {
	Socket socket;
	BufferedReader inputReader;
	PrintWriter requestSender;
	DataInputStream responseReader;
	ProjectUtils pu;
	String userInfo = null;
	List<PostBean> posts = null;

	public FrontEnd() {
		this.connectToServer();
		this.pu = new ProjectUtils();
		this.main();
	}
	public void connectToServer() {
		while (true) {
			try {
				this.inputReader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("서버 주소를 입력해주세요 (192.168.0.122): ");
				String host = this.getInput();
				System.out.println("포트번호를 입력해주세요 (2400): ");
				int port = Integer.parseInt(this.getInput());
				this.socket = new Socket(host, port);
				System.out.println("서버에 연결되었습니다.");
				this.requestSender = new PrintWriter(socket.getOutputStream(), true);
				this.responseReader = new DataInputStream(socket.getInputStream());
				break;
			} catch (IOException e) {
				System.out.println("서버연결에 실패하였습니다.\n" + "호스트의 IP주소를 확인해주세요.\n" + "계속 하려면 Enter키를 눌러주세요.");
				this.getInput();
				continue;
			}
		}
	}
	void main() {
		String[] options = { "게시판  ", "로그인  ", "회원가입 " };
		String[] options2 = { "게시판  ", "로그아웃  ", "마이페이지" };
		String select;
		while (true) {
			try {
				System.out.println(this.pu.getTitle());
				if (this.userInfo != null) {
					System.out.println(this.pu.getMenu(options2, true));
				} else {
					System.out.println(this.pu.getMenu(options, true));
				}
				select = this.inputReader.readLine();
				switch (select) {
				case "1":
					this.moveForum();
					break;
				case "2":
					if (this.userInfo == null) {
						this.moveLogIn();
					} else {
						this.moveLogOut();
					}
					break;
				case "3":
					if (this.userInfo == null) {
						this.moveSignUp();
					} else {
						this.moveMyPage();
					}
					break;
				case "0":
					System.out.println("종료");
					System.exit(0);
					break;
				}
			} catch (Exception e) {
				System.out.println("システムエラー");
				continue;
			}
		}
	}
	private void moveMyPage() {
		String[] options = { "홈페이지 " };
		System.out.println(pu.getTitle("마이페이지", false));
		String select;
		boolean run = true;
		while (run) {
			System.out.println("내 아이디: " + ((this.userInfo != null) ? this.userInfo : "로그인 후 이용해주세요."));
			System.out.println("==================================");
			System.out.println(this.pu.getMenu(options, true));
			select = this.getInput();
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
		System.out.println("1. 로그아웃 확인	2. 취소");
		String select = this.getInput();
		if (select.equals("1")) {
			userInfo = null;
			System.out.println("로그아웃되었습니다.");
			System.out.println("1. 확인");
			this.getInput();
		}
	}
	private void moveSignUp() {
		System.out.println(this.pu.getTitle("회원가입", true));
		String[] names = { "id", "pw", "birthday", "recoveryQ", "recoveryA" };
		String[] data = new String[5];
		String clientData;
		String serverData;
		boolean isExit = false;
		while (!isExit) {
			System.out.println("아이디: ");
			data[0] = this.getInput();
			if (isExit = this.pu.exitCheck(data[0]))
				break;
			if (this.pu.containsComma(data[0]))
				continue;
			clientData = this.pu.makeTransferData("isIdUsed", names[0], data[0]);
			this.sendRequest(clientData);
			serverData = this.getResponse();
			if (serverData.equals("false")) {
				System.out.println("사용 가능한 아이디입니다.");
				this.pu.confirm();
				break;
			} else {
				System.out.println("이미 사용중인 아이디입니다.");
			}
		}

		isExit = this.pu.confirmInput(isExit, this.inputReader);

		while (!isExit) {
			System.out.println("비밀번호: ");
			data[1] = this.getInput();
			if (isExit = this.pu.exitCheck(data[1]))
				break;
			if (this.pu.containsComma(data[1]))
				continue;
			System.out.println("비밀번호 확인: ");
			String confirm = this.getInput();
			if (isExit = this.pu.exitCheck(confirm))
				break;
			if (data[1].equals(confirm)) {
				break;
			} else {
				System.out.println("비밀번호가 다릅니다.");
			}
		}

		while (!isExit) {
			System.out.println("생년월일(19910101):  ");
			data[2] = this.getInput();
			if (isExit = this.pu.exitCheck(data[2]))
				break;
			if (this.pu.containsComma(data[2]))
				continue;
			if (!this.pu.isNum(data[2])) {
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
			System.out.println(this.pu.getMenu(recoveryQ, false));
		}
		int recoveryQIdx = -1;
		while (!isExit) {
			try {
				recoveryQIdx = Integer.parseInt(this.getInput());
				if (isExit = this.pu.exitCheck(recoveryQIdx))
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
			data[4] = this.getInput();
			if (this.pu.containsComma(data[4]))
				continue;
			break;
		}

		if (!(isExit = this.pu.exitCheck(data[4]))) {
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
			this.pu.confirm();
		}

		if (!(isExit = this.pu.confirmInput(isExit, this.inputReader))) {
			clientData = this.pu.makeTransferData("signUp", names, data);
			this.sendRequest(clientData);
			serverData = this.getResponse();
			if (serverData.equals("true")) {
				System.out.println(this.pu.getTitle("가입 성공", false));
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

		System.out.println(this.pu.getTitle("로그인", true));
		while (!isExit) {
			System.out.println("아이디 : ");
			data[0] = this.getInput();
			if (isExit = this.pu.exitCheck(data[0]))
				break;
			System.out.println("비밀번호: ");
			data[1] = this.getInput();
			if (isExit = this.pu.exitCheck(data[1]))
				break;
			this.pu.confirm();
			if (!(isExit = this.pu.confirmInput(isExit, this.inputReader))) {
				clientData = this.pu.makeTransferData("logIn", names, data);
				this.sendRequest(clientData);
				serverData = this.getResponse();
				if (!serverData.equals("false")) {
					this.userInfo = serverData;
					System.out.println(this.pu.getTitle());
					System.out.println(this.pu.getAccessInfo(serverData));
					break;
				} else {
					System.out.println("인증 정보가 잘못되었습니다.");
				}
			}
		}
	}
	private void moveForum() {
		// 로그인 게시판
		String[] options = { "글쓰기  ", "새로고침  ", "홈페이지 " };
		// 로그아웃 게시판
		String[] options2 = { "홈페이지 ", "새로고침  " };
		// 로그인 게시글
		String[] options3 = { "댓글달기 ", "새로고침  ", "글삭제    ", "돌아가기 " };
		// 로그아웃 게시글
		String[] options4 = { "새로고침  ", "돌아가기 " };
		boolean showAccess = true;

		System.out.println(this.pu.getTitle("게시판", false));
		String select;
		boolean run = true;

		while (run) {
			this.getPosts();
			System.out.println(getList());
			if (this.userInfo != null) {
				System.out.println(this.pu.getMenu(options, true));
			} else {
				System.out.println(this.pu.getMenu(options2, true));
			}
			select = this.getInput();
			switch (select) {
			case "1":
				if (this.userInfo != null) {
					this.addPost(showAccess);
				} else {
					run = false;
				}
				break;
			case "2":
				// 새로고침
				break;
			case "3":
				if (this.userInfo != null)
					run = false;
				break;
			case "0":
				System.out.println("종료");
				System.exit(0);
				break;
			default:
				try {
					Integer.parseInt(select);
					this.showPostDetail(select, options3, options4);
				} catch (Exception e) {
					break;
				}
			}
		}
	}
	void showPostDetail(String idx, String[] options3, String[] options4) {
		boolean run = true;
		while (run) {
			getPosts();
			PostBean po = null;
			try {
				for (PostBean p : this.posts) {
					if (p.getIndex() == Integer.parseInt(idx)) {
						po = p;
						break;
					}
				}

			} catch (Exception e) {
				System.out.println("오류");
				System.out.println("1. 확인");
				this.getInput();
				return;
			}

			StringBuilder sb = new StringBuilder();
			sb.append("==================================\n");
			sb.append(po.getTitle() + "\n");
			sb.append(po.getUser() + " | " + po.getDate() + " " + po.getTime() + "\n");
			sb.append("----------------------------------\n");
			sb.append(po.getContent());
			sb.append("==================================\n");
			for (CommentBean c : po.getComments()) {
				sb.append(c.getCommentIdx() + " | ");
				sb.append(c.getUser() + " : ");
				sb.append(c.getContent() + "\n                            ");
				sb.append((this.pu.getDate().equals(c.getDate()) ? c.getTime() : c.getDate().substring(5, 10)) + "\n");
				sb.append(
						po.getComments().get(po.getComments().size() - 1) != c ? "----------------------------------\n"
								: "==================================");
			}
			System.out.println(sb.toString());
			String select;
			sb.setLength(0);
			sb.append(pu.getMenu((this.userInfo != null) ? options3 : options4, true));
			sb.append("\n==================================");
			System.out.println(sb);
			select = this.getInput();
			switch (select) {
			case "1":
				// 댓글 : 새로고침
				if (this.userInfo != null) {
					this.addComment(po.getIndex());
				}
				break;
			case "2":
				// 새로고침 : 돌아가기
				if (this.userInfo == null) {
					run = false;
				}
				break;
			case "3":
				// 글삭 : 아무것도
				if (this.userInfo != null) {
					if (!this.userInfo.equals(po.getUser())) {
						System.out.println("권한이 없습니다.");
						System.out.println("1. 확인");
						this.getInput();
					} else {
						deletePost(po.getIndex());
					}
				}
				break;
			case "4":
				if (this.userInfo != null) {
					run = false;
				}
				// 돌아가기 :아무것도
				break;
			case "0":
				// 종료
				System.out.println("종료");
				System.exit(0);
				break;
			}
		}
	}
	void addComment(int idx) {
		String clientData = null;
		String serverData = null;
		String[] names = { "postIdx", "commentIdx", "user", "content", "date", "time" };
		String[] data = new String[6];

		data[0] = String.valueOf(idx);

		this.sendRequest("getNextCommentIdx?postIdx=" + idx);
		serverData = this.getResponse();
		data[1] = serverData;

		data[2] = this.userInfo;

		System.out.print("댓글: ");
		data[3] = this.getInput();

		this.pu.confirm();
		boolean isExit = false;
		if (!(isExit = this.pu.confirmInput(isExit, this.inputReader))) {
			data[4] = this.pu.getDate();
			data[5] = this.pu.getTime();

			clientData = this.pu.makeTransferData("addComment", names, data);
			this.sendRequest(clientData);
			serverData = this.getResponse();
			if (serverData.equals("true")) {
				System.out.println("댓글이 추가되었습니다.");
			} else {
				System.out.println("오류");
			}
		}
	}
	void deletePost(int idx) {
		String serverData = null;
		System.out.println("정말 삭제하시겠습니까?");
		this.pu.confirm();
		boolean isExit = false;
		if (!(isExit = this.pu.confirmInput(isExit, this.inputReader))) {
			this.sendRequest("deletePost?postIdx=" + idx);
			serverData = this.getResponse();
			System.out.println(serverData != "false" ? "게시글이 삭제되었습니다." : "오류");
			System.out.println("1. 확인");
			this.getInput();
		}		
	}
	void getPosts() {
		String serverDataPosts = null;
		String serverDataComments = null;
		this.sendRequest("getPosts");
		serverDataPosts = this.getResponse();

		StringTokenizer st;
		StringTokenizer st2;
		this.posts = new ArrayList<>();
		PostBean p;
		CommentBean c;
		List<CommentBean> comments;
		String content;
		st = new StringTokenizer(serverDataPosts, "|\n");
		while (st.hasMoreTokens()) {
			p = new PostBean();
			p.setIndex(Integer.parseInt(st.nextToken()));
			p.setUser(st.nextToken());
			p.setTitle(st.nextToken());
			content = st.nextToken();
			p.setContent(content.replace("<newLine>", "\n"));
			p.setDate(st.nextToken());
			p.setTime(st.nextToken());

			comments = new LinkedList<>();
			this.sendRequest("getComments?postIdx=" + p.getIndex());
			serverDataComments = this.getResponse();
			st2 = new StringTokenizer(serverDataComments, "|\n");

			while (st2.hasMoreTokens()) {
				c = new CommentBean();
				c.setPostIdx(Integer.parseInt(st2.nextToken()));
				c.setCommentIdx(Integer.parseInt(st2.nextToken()));
				c.setUser(st2.nextToken());
				c.setContent(st2.nextToken());
				c.setDate(st2.nextToken());
				c.setTime(st2.nextToken());
				comments.add(c);
			}
			p.setComments(comments);
			this.posts.add(p);
		}
	}
	String getList() {
		StringBuffer sb = new StringBuffer();
		// 0. 1. 번은 홈페이지, 종료 기능이므로 2번 부터 게시글 선택
		// 2. 아름다운 구속 댓글수
		// 틀니 | 13:15/2023.03.02 | 조회 99 | 추천 99
		for (int i = 0; i < this.posts.size(); i++) {
			sb.append(this.posts.get(i).getIndex() + ". ");
			sb.append(this.posts.get(i).getTitle() + "\n");
			sb.append(this.posts.get(i).getUser() + "\t");
			sb.append((this.pu.getDate().equals(this.posts.get(i).getDate()) ? this.posts.get(i).getTime()
					: this.posts.get(i).getDate().substring(5, 10)));
			// sb.append("조회 "); 조회수, 추천수, 댓글수는 여유되면 추가
			sb.append((i < this.posts.size() - 1) ? "\n----------------------------------\n"
					: "\n==================================");
		}
		return sb.toString();
	}

	void addPost(boolean showAccess) {
		String clientData;
		String serverData;
		String[] names = { "번호", "작성자", "제목", "내용", "작성날짜", "작성시간" };
		String[] data = new String[6];

		// 최신글 번호 불러오는 코드 추가
		this.sendRequest("getNextPostIdx");
		serverData = this.getResponse();
		data[0] = serverData;

		data[1] = this.userInfo;
		data[2] = "";
		System.out.print("제목 : ");
		data[2] = this.getInput();
		System.out.print("내용 (1. 입력 종료): ");
		while (true) {
			data[3] = this.pu.nextPara(inputReader);
			if (data[3].length() < 1) {
				System.out.println("내용을 입력해주세요.");
				continue;
			}
			break;
		}	
		this.pu.confirm();
		boolean isExit = false;

		if (!(isExit = this.pu.confirmInput(isExit, this.inputReader))) {
			data[4] = this.pu.getDate();
			data[5] = this.pu.getTime();
			clientData = this.pu.makeTransferData("addPost", names, data);
			this.sendRequest(clientData);
			serverData = this.getResponse();
			if (serverData.equals("true")) {
				System.out.println("글이 정상적으로 등록되었습니다.");
				System.out.println("==================================");
				showAccess = false;
			}
		}
	}
	String getInput() {
		try {
			return this.inputReader.readLine();
		} catch (Exception e) {
			System.out.println("getInput");
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}
	void sendRequest(String clientData) {
		this.requestSender.println(clientData);
		// print won't work, flush doesn't help
	}
	String getResponse() {
		try {
			// return responseReader.readLine();
			return this.responseReader.readUTF();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}
}
