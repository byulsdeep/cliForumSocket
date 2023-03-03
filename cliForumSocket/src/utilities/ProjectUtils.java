package utilities;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ProjectUtils {
	public String getDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
	}
	public String getTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
	}
	public int getTagIndex(String tag) {
		tag = "<" + tag + ">";
		return tag.indexOf(tag) + tag.length();
	}
	public String nextPara(BufferedReader reader) {
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while (!(line = reader.readLine()).equals("1")) {
				sb.append(line);
				sb.append("<newLine>");
			}
		} catch (Exception e) {
		}
//		if (sb.length() > 8)
//		sb.delete(sb.length() - 9, sb.length());
		return sb.toString();
	}
	public String getJobCode(String data) {
		return data.contains("?") ? data.substring(0, data.indexOf("?")) : data;
	}
	public String getAccessInfo(String userInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("                 " + userInfo + "님, 어서오세요!");
		return sb.toString();
	}
	public String[][] extractData(String data) {
		// signUp?id=byulsdeep&pw=*****&birthday=19940602&recoveryQ=1&recoveryA=yanghwa
		StringTokenizer st = new StringTokenizer(data.substring(data.indexOf("?") + 1), "=&");
		String[][] exData = new String[st.countTokens() / 2][2];
		if (st.countTokens() < 2) {
			return null;
		}
		for (int i = 0; st.hasMoreTokens(); i++) {
			exData[i][0] = st.nextToken();
			exData[i][1] = st.nextToken();
		}
		return exData;
	}
	public void confirm() {
		String[] options = { "확인", "취소" };
		System.out.println(getMenu(options, false));
	}

	public boolean confirmInput(boolean isExit, BufferedReader reader) {
		String select;
		while (!isExit) {
			try {
				select = reader.readLine();
				if (select.equals("0") || select.equals("2")) {
					isExit = true;
					break;
				}
				if (!select.equals("1"))
					continue;
				break;
			} catch (Exception e) {
				continue;
			}
		}
		return isExit;
	}

	public boolean containsComma(String s) {
		if (s.contains(",")) {
			System.out.println(s + "는(은) 금지어입니다.");
			return true;
		}
		return false;
	}
	public boolean exitCheck(String s) {
		if (s != null)
			return s.equals("0");
		return true;
	}
	public boolean exitCheck(int i) {
		return i == 0;
	}
	public String getMenu(String[] options, boolean exitButton) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < options.length; i++) {
			sb.append(i + 1 + ". " + options[i]);
			sb.append((i % 2 == 0 && options[i].length() < 10) ? "\t" : "\n");
		}
		sb.append((exitButton) ? "0. 종료   " : "");
		return sb.toString();
	}
	public String getTitle(String title, boolean cancelButton) {
		StringBuffer sb = new StringBuffer();
		sb.append("==================================\r\n" + "\r\n" + "　　　                     " + title + "\r\n"
				+ "                          " + ((cancelButton) ? "0. 나가기" : "") + "\r\n"
				+ "==================================");

		return sb.toString();
	}
	public String getTitle() {
		StringBuffer sb = new StringBuffer();

		sb.append("==================================\r\n" + "\r\n" + "　　　            CLI Forum v1.0\r\n" + "\r\n"
				+ "　　　　            designed by\r\n" + "\r\n" + "+-++-++-++-++-++-++-++-++-++-++-+\r\n"
				+ "|B||y||u||l||s||D||e||e||p||★||彡|\r\n" + "+-++-++-++-++-++-++-++-++-++-++-+\r\n"
				+ "==================================");
		return sb.toString();
	}
	public void scannerClear(Scanner sc) {
		if (sc.hasNextLine())
			sc.nextLine();
	}
	public String makeTransferData(String jobCode, String name, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(jobCode + "?");
		sb.append(name + "=" + data);
		return sb.toString();
	}
	public String makeTransferData(String jobCode, String[] names, String[] data) {
		StringBuffer sb = new StringBuffer();
		sb.append(jobCode + "?");
		for (int i = 0; i < names.length; i++) {
			sb.append(names[i] + "=");
			sb.append((i < names.length - 1) ? data[i] + "&" : data[i]);
		}
		return sb.toString();
	}
	public boolean isNum(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean isEmpty(String s) {
		return s.length() == 0 ? true : false;
	}

}
