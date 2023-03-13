package com.diligence.dfs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Diligence
 * @create 2023 - 03 - 14 1:32
 */

@Getter
@AllArgsConstructor
public enum FsOpType {
	/**
	 * 创建文件夹
	 */
	MKDIR(1),
	/**
	 * 创建文件
	 */
	CREATE(2),

	/**
	 * 删除文件或者文件夹
	 */
	DELETE(3),
	;

	private int value;
}
