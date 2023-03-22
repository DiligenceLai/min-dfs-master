package com.diligence.dfs.namenode.editslog;

import java.io.IOException;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 1:00
 */
public class DoubleBuffer {
	// 以下定义了两块缓冲区
	private EditLogBuffer currentBuffer;
	private EditLogBuffer syncBuffer;

	/**
	 * 写入一条editlog
	 */
	public void write(EditLogWrapper editLog) throws IOException {
		currentBuffer.write(editLog);
	}

	/**
	 * 交换两块缓冲区
	 */
	public void setReadyToSync() {
		EditLogBuffer temp = currentBuffer;
		currentBuffer = syncBuffer;
		syncBuffer = temp;
	}

	/**
	 * 把缓冲区的 editlog 数据刷新到磁盘
	 */
	public EditslogInfo flush() throws IOException {
		// 刷盘
		EditslogInfo editslogInfo = syncBuffer.flush();
		// 如果 editslogInfo 不为空，说明已经有数据成功刷盘了
		// 这时候可以清楚缓冲区中的数据了
		if (editslogInfo != null) {
			syncBuffer.clear();
		}
		// 返回 editslog 文件信息
		return editslogInfo;
	}
}
