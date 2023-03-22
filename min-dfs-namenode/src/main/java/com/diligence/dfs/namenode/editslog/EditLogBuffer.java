package com.diligence.dfs.namenode.editslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 0:49
 */
public class EditLogBuffer {
	private ByteArrayOutputStream buffer;

	private volatile long startTxid = -1L;
	private volatile long endTxid = 0L;
	/**
	 * 写入一条数据到缓冲区
	 *
	 * @param editLog editlog
	 * @throws IOException IO异常
	 */
	public void write(EditLogWrapper editLog) throws IOException {
		if (startTxid == -1) {
			startTxid = editLog.getTxId();
		}
		endTxid = editLog.getTxId();
		// 将一条数据写入到先前定义的字节数组输出流
		buffer.write(editLog.toByteArray());
	}

	/**
	 * 获取当前缓冲区的 EditLog
	 *
	 * @return 当前缓冲区的EditLog
	 */
	public List<EditLogWrapper> getCurrentEditLog() {
		// 将字节数组输出流转为字符数据
		byte[] bytes = buffer.toByteArray();
		if (bytes.length == 0) {
			return new ArrayList<>();
		}
		// 解析字符数组，返回 editLog
		return EditLogWrapper.parseFrom(bytes);
	}
}
