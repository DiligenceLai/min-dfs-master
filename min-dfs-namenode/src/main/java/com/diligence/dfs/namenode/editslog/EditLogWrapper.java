package com.diligence.dfs.namenode.editslog;

import com.ruyuan.dfs.common.utils.ByteUtil;
import com.ruyuan.dfs.common.utils.PrettyCodes;
import com.ruyuan.dfs.model.backup.EditLog;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 0:43
 */
public class EditLogWrapper {
	// EditLog ：proto 生成的对象
	private EditLog editLog;

	// 构造方法
	public EditLogWrapper(int opType, String path) {
		this(opType, path, new HashMap<>(PrettyCodes.trimMapSize()));
	}

	/**
	 * 构造一条 editlog
	 * @param opType 对文件的操作类型
	 * @param path 路径
	 * @param attr 属性
	 */
	public EditLogWrapper(int opType, String path, Map<String, String> attr) {
		this.editLog = EditLog.newBuilder()
				.setOpType(opType)
				.setPath(path)
				.putAllAttr(attr)
				.build();
	}

	public EditLogWrapper(EditLog editLog) {
		this.editLog = editLog;
	}

	public long getTxId() {
		return this.editLog.getTxId();
	}

	public byte[] toByteArray() {
		byte[] body = editLog.toByteArray();
		int bodyLength = body.length;
		byte[] ret = new byte[body.length + 4];
		ByteUtil.setInt(ret, 0, bodyLength);
		System.arraycopy(body, 0, ret, 4, bodyLength);
		return ret;
	}
}
