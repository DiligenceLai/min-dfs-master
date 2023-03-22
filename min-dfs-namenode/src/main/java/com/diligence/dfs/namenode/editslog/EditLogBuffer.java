package com.diligence.dfs.namenode.editslog;

import com.diligence.dfs.config.NameNodeConfig;
import com.ruyuan.dfs.common.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 0:49
 */
@Slf4j
public class EditLogBuffer {

	private final NameNodeConfig nameNodeConfig;
	private ByteArrayOutputStream buffer;

	private volatile long startTxid = -1L;
	private volatile long endTxid = 0L;


	/**
	 * 在创建 EditLogBuffer 的时候，
	 *  1. 设置 NameNodeConfig
	 *  2. 新建一个字节数组输出流
	 * @param nameNodeConfig
	 */
	public EditLogBuffer(NameNodeConfig nameNodeConfig) {
		this.nameNodeConfig = nameNodeConfig;
		this.buffer = new ByteArrayOutputStream((nameNodeConfig.getEditLogFlushThreshold() * 2));
	}

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

	/**
	 * 清除缓冲区
	 */
	public void clear() {
		// 将缓冲区复位
		buffer.reset();

		// 重置两个指针
		startTxid = -1;
		endTxid = -1;
	}

	/**
	 * 刷磁盘
	 */
	public EditslogInfo flush() throws IOException {
		// 缓冲区没有数据
		if (buffer.size() <= 0) {
			return null;
		}
		// buffer：ByteArrayOutputStream
		// 将字节数组输出流转为字节数组
		byte[] data = buffer.toByteArray();
		// 将数据打包进一个字节缓冲区
		ByteBuffer dataBuffer = ByteBuffer.wrap(data);
		// 0~500.log 即 startTxid~endTxid.log
		String path = nameNodeConfig.getEditlogsFile(startTxid, endTxid);
		log.info("保存editslog文件：[file={}]", path);
		FileUtil.saveFile(path, false, dataBuffer);
		// 返回 editslog 文件信息
		return new EditslogInfo(startTxid, endTxid, path);
	}
}
