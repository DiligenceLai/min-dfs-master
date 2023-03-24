package com.diligence.dfs.fs;

import com.diligence.dfs.namenode.fs.Node;
import com.ruyuan.dfs.common.enums.NodeType;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 负责管理内存中文件目录树的核心组件
 * @author Diligence
 * @create 2023 - 03 - 25 0:40
 */
public class FsDirectory {
	private Node root;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	public FsDirectory() {
		// 创建一个根目录
		this.root = new Node("/", NodeType.DIRECTORY.getValue());
	}
}
