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
		StringBuffer sb = new StringBuffer("");
		String record = null;
		try {
			// admin,admin,19940602,1,양화초등학교 ->
			// admin,id2,id3,id4.....
			for (int i = 0; (record = this.bReader.readLine()) != null; i++) {
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
		String userInfo = "false";
		try {
			while ((record = this.bReader.readLine()) != null) {
				exRecord = record.split(",");
				if (exData[0][1].equals(exRecord[0]) && exData[1][1].equals(exRecord[1])) {
					userInfo = exData[0][1];
					break;
				}
			}
		} catch (Exception e) {
		}
		return userInfo;
	}
	public String getPosts(ProjectUtils pu) {
		String record = null;
		StringBuilder sb = new StringBuilder("");
		try {
			while ((record = this.bReader.readLine()) != null) {
				sb.append(record);
				sb.append("\n");
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	public String getComments() { // get all comments
		String record = null;
		StringBuilder sb = new StringBuilder("");
		try {
			while ((record = this.bReader.readLine()) != null) {
				sb.append(record);
				sb.append("\n");
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	public String getComments(String postIdx) { // get comments based on post idx
		String record = null;
		StringBuilder sb = new StringBuilder("");
		try {
			while ((record = this.bReader.readLine()) != null) {
				if (postIdx.equals(record.substring(0, record.indexOf("|")))) {
					sb.append(record);
					sb.append("\n");
				}
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	public int getNextPostIdx(ProjectUtils pu) {
		String record = null;
		List<Integer> idxs = new ArrayList<>();
		try {
			while ((record = this.bReader.readLine()) != null) {
				idxs.add(Integer.parseInt(record.substring(0, record.indexOf("|"))));
			}
		} catch (Exception e) {
		}
		return idxs.size() < 1 ? 1 : Collections.max(idxs) + 1;
	}
	public int getNextCommentIdx(String postIdx, ProjectUtils pu) {
		String record = null;
		List<Integer> idxs = new ArrayList<>();
		StringTokenizer st = null;
		try {
			while ((record = this.bReader.readLine()) != null) {
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
		}
		return idxs.size() < 1 ? 1 : Collections.max(idxs) + 1;
	}
	public boolean insPost(String data) {
		boolean isWrite = false;
		try {
			this.bWriter.write(data);
			this.bWriter.newLine();
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
			this.bWriter.write(sb.toString());
			this.bWriter.newLine();
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
			this.bWriter.write(sb.toString());
			this.bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}
	public boolean insComment(String data) {
		try {
			this.bWriter.write(data);
			this.bWriter.newLine();
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
			this.bWriter.write(sb.toString());
			this.bWriter.newLine();
			isWrite = true;
		} catch (Exception e) {
		}
		return isWrite;
	}

	public boolean fileConnected(boolean readOrWrite, String fileName, boolean append) {
		boolean result;
		String ap = new File("").getAbsolutePath();
		this.file = new File(ap + fileName);
		try {
			if (readOrWrite) {
				this.bReader = new BufferedReader(new FileReader(this.file));
			} else {
				if (append) {
					this.writer = new FileWriter(this.file, true); // 파일 내용 추가
				} else {
					this.writer = new FileWriter(this.file); // 파일 내용 덮어쓰기
				}
				this.bWriter = new BufferedWriter(this.writer);
			}
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public void fileClose(boolean readOrWrite) {
		if (readOrWrite) {
			try {
				if (this.bReader != null) {
					this.bReader.close();
				}
			} catch (Exception e2) {
			}
			try {
				if (this.reader != null) {
					this.reader.close();
				}
			} catch (Exception e) {
			}
		} else {
			try {
				if (this.bWriter != null) {
					this.bWriter.close();
				}
			} catch (Exception e2) {
			}
			try {
				if (this.writer != null) {
					this.writer.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
