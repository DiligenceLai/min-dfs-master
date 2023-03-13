package com.diligence.dfs.namenode.fs;

import com.ruyuan.dfs.common.enums.NodeType;
import com.ruyuan.dfs.model.backup.INode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 文件目录树，逻辑元数据
 * @author Diligence
 * @create 2023 - 03 - 14 1:42
 */

@Data
@Slf4j
public class Node {
	// 路径
	// 应该就是文件名
	private String path;
	// 文件 / 目录
	private int type;
	// 用一个 TreeMap 来存储子节点（子目录）
	private final TreeMap<String, Node> children;
	// 存储当前结点的属性（文件属性）
	private Map<String, String> attr;
	// 当前结点的父节点（上一级目录）
	private Node parent;

	public Node() {
		this.children = new TreeMap<>();
		this.attr = new HashMap<>();
		this.parent = null;
	}

	public Node(String path, int type) {
		this();
		this.path = path;
		this.type = type;
	}

	/**
	 * 是否是一个文件
	 *
	 * @return 是否是一个文件
	 */
	public boolean isFile() {
		return type == NodeType.FILE.getValue();
	}

	/**
	 * 获取当前节点的全名路径
	 *
	 * @return 当前节点的全路径
	 */
	public String getFullPath() {
		return getFullPathInternal(this);
	}

	// 获取给定节点的全名路径
	private String getFullPathInternal(Node parent) {
		if (parent == null) {
			return null;
		}
		// 递归一直到找到根目录
		String parentPath = getFullPathInternal(parent.getParent());
		if (parentPath == null) {
			return "";
		}
		// 拼接路径
		// 从根目录到当前目录的路径
		return parentPath + "/" + parent.path;
	}

	/**
	 * 实现 node -> iNode 的转变
	 * Node 的序列化为 INode
	 * @param node
	 * @return
	 */
	public static INode toINode(Node node) {
		// proto 生成对象
		INode.Builder builder = INode.newBuilder();
		// 结点的路径属性
		String path = node.getPath();
		// 结点的类型（文件、目录）
		int type = node.getType();

		// 根据 node 属性 设置 iNode 属性
		builder.setPath(path);
		builder.setType(type);
		builder.putAllAttr(node.getAttr());

		// 用一个集合存储 node 的子节点
		Collection<Node> children = node.getChildren().values();
		if (children.isEmpty()) {
			// 创建一个 iNode 对象
			return builder.build();
		}
		List<INode> tmpNode = new ArrayList<>(children.size());
		for (Node child : children) {
			// 递归
			INode iNode = toINode(child);
			tmpNode.add(iNode);
		}
		builder.addAllChildren(tmpNode);
		// 创建一个 iNode 对象
		return builder.build();
	}

	public static Node parseINode(INode iNode) {
		return parseINode(iNode, null);
	}

	/**
	 *  根据 FSImage 初始化内存目录树
	 *  实现了从 iNode -> node 的转变
	 * @param iNode
	 * @param parent
	 * @return
	 */
	public static Node parseINode(INode iNode, String parent) {
		Node node = new Node();
		if (parent != null && log.isDebugEnabled()) {
			log.debug("parseINode executing :[path={},  type={}]", parent, node.getType());
		}

		String path = iNode.getPath();
		int type = iNode.getType();

		node.setPath(path);
		node.setType(type);
		node.putAllAttr(iNode.getAttrMap());

		List<INode> children = iNode.getChildrenList();
		if (children.isEmpty()) {
			return node;
		}
		for (INode child : children) {
			node.addChildren(parseINode(child, parent == null ? null : parent + "/" + child.getPath()));
		}
		return node;
	}


	/**
	 * 深度拷贝节点
	 *
	 * @param node  节点
	 * @param level 拷贝多少个孩子层级
	 * @return 拷贝节点
	 */
	public static Node deepCopy(Node node, int level) {
		if (node == null) {
			return null;
		}

		// 复制 node 结点到 ret 结点
		Node ret = new Node();
		String path = node.getPath();
		int type = node.getType();
		ret.setPath(path);
		ret.setType(type);
		ret.putAllAttr(node.getAttr());

		// 通过 level 控制是否复制子目录
		if (level > 0) {
			TreeMap<String, Node> children = node.children;
			if (!children.isEmpty()) {
				for (String key : children.keySet()) {
					ret.addChildren(deepCopy(children.get(key), level - 1));
				}
			}
		}
		return ret;
	}

	/**
	 * 添加一个孩子节点
	 *
	 * @param child 孩子节点
	 */
	public void addChildren(Node child) {
		synchronized (children) {
			child.setParent(this);
			this.children.put(child.getPath(), child);
		}
	}

	/**
	 * 获取孩子接地那
	 *
	 * @param child 孩子节点
	 */
	public Node getChildren(String child) {
		synchronized (children) {
			return children.get(child);
		}
	}

	public void putAllAttr(Map<String, String> attr) {
		this.attr.putAll(attr);
	}

	@Override
	public String toString() {
		return "Node{" +
				"path='" + path + '\'' +
				", type=" + type +
				", children=" + children +
				", attr=" + attr +
				'}';
	}
}

