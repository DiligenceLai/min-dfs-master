package com.diligence.dfs.fs;

import com.diligence.dfs.namenode.fs.Node;
import com.ruyuan.dfs.common.enums.NodeType;
import com.ruyuan.dfs.common.utils.StringUtils;

import java.util.Map;
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

	/**
	 * 创建文件目录
	 *
	 * @param path 文件目录
	 */
	public void mkdir(String path, Map<String, String> attr) {
		try {
			// 加写锁
			lock.writeLock().lock();
			String[] paths = StringUtils.split(path, '/');
			Node current = root;
			for (String p : paths) {
				if ("".equals(p)) {
					continue;
				}
				current = findDirectory(current, p);
			}
			current.putAllAttr(attr);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private Node findDirectory(Node current, String p) {
		// 获取当前结点的子节点
		Node childrenNode = current.getChildren(p);
		if (childrenNode == null) {
			// 创建了个目录
			childrenNode = new Node(p, NodeType.DIRECTORY.getValue());
			// 添加一个孩子结点
			current.addChildren(childrenNode);
		}
		current = childrenNode;
		return current;
	}
}
