package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import utilities.ProjectUtils;

public class DataAccessObject {
	File file;
	FileReader reader;
	FileWriter writer;
	BufferedReader bReader;
	BufferedWriter bWriter;

	public String getIdList() {
		StringBuffer sb = new StringBuffer();
		String record = null;
		try {
			// admin,admin,19940602,1,양화초등학교 ->
			// admin,id2,id3,id4.....
			for (int i = 0; (record = bReader.readLine()) != null; i++) {
				sb.append((i > 0) ? "," : "");
				sb.append(record.substring(0, record.indexOf(",")));
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	public String getUserInfo(String[][] exData) {
		String record = null;
		String[] exRecord;
		String userInfo = null;
		try {
			while ((record = bReader.readLine()) != null) {
				exRecord = record.split(",");
				if (exData[0][1].equals(exRecord[0]) && exData[1][1].equals(exRecord[1])) {
					userInfo = exData[0][1];
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	public String getPosts(ProjectUtils pu) {
		String record = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((record = bReader.readLine()) != null) {
				sb.append(record);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public String getComments() { // get all comments
		String record = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((record = bReader.readLine()) != null) {
				sb.append(record);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public String getComments(String postIdx) { // get comments based on post idx
		String record = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((record = bReader.readLine()) != null) {
				if (postIdx.equals(record.substring(0, record.indexOf("|")))) {
					sb.append(record);
					sb.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public int getNextPostIdx(ProjectUtils pu) {
		String record = null;
		List<Integer> idxs = new ArrayList<>();
		try {
			while ((record = bReader.readLine()) != null) {
				idxs.add(Integer.parseInt(record.substring(0, record.indexOf("|"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idxs.size() < 1 ? 1 : Collections.max(idxs) + 1;
	}
	public int getNextCommentIdx(String postIdx, ProjectUtils pu) {
		String record = null;
		List<Integer> idxs = new ArrayList<>();
		StringTokenizer st = null;
		try {
			while ((record = bReader.readLine()) != null) {
				try {
					st = new StringTokenizer(record, "|");
					if (st.nextToken().equals(postIdx)) {
						idxs.add(Integer.parseInt(st.nextToken()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idxs.size() < 1 ? 1 : Collections.max(idxs) + 1;
	}
	public boolean insPost(String data) {
		boolean isWrite = false;
		try {
			bWriter.write(data);
			bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}
	public boolean insPost(String[][] exData) {
		boolean isWrite = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < exData.length; i++) {
			sb.append(exData[i][1]);
			sb.append((i < exData.length - 1) ? "|" : "");
		}
		try {
			bWriter.write(sb.toString());
			bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}
	public boolean insComment(String[][] exData) {
		boolean isWrite = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < exData.length; i++) {
			sb.append(exData[i][1]);
			sb.append((i < exData.length - 1) ? "|" : "");
		}
		try {
			bWriter.write(sb.toString());
			bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}
	public boolean insComment(String data) {
		try {
			bWriter.write(data);
			bWriter.newLine();
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	public boolean insUser(String[][] exData) {
		boolean isWrite = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < exData.length; i++) {
			sb.append(exData[i][1]);
			sb.append((i < exData.length - 1) ? "," : "");
		}
		try {
			bWriter.write(sb.toString());
			bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}

	public boolean fileConnected(boolean readOrWrite, String fileName, boolean append) {
		boolean result;
		String ap = new File("").getAbsolutePath();
		file = new File(ap + fileName);
		try {
			if (readOrWrite) {
				bReader = new BufferedReader(new FileReader(file));
			} else {
				if (append) {
					writer = new FileWriter(file, true); // 파일 내용 추가
				} else {
					writer = new FileWriter(file); // 파일 내용 덮어쓰기
				}
				bWriter = new BufferedWriter(writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		result = true;
		return result;
	}

	public void fileClose(boolean readOrWrite) {
		if (readOrWrite) {
			try {
				if (bReader != null) {
					bReader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (bWriter != null) {
					bWriter.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
