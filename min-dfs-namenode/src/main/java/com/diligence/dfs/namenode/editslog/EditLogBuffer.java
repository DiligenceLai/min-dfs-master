package com.diligence.dfs.namenode.editslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
}
