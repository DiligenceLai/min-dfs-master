package com.diligence.dfs.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * NameNode节点启动模式
 * 单机 vs 集群
 * @author Diligence
 * @create 2023 - 03 - 14 1:32
 */
@Getter
@AllArgsConstructor
public enum NameNodeLaunchMode {

	/**
	 * NameNode节点启动模式
	 */
	SINGLE(1, "single", "单机模式"),
	CLUSTER(2, "cluster", "集群模式");

	private int value;
	private String mode;
	private String desc;

	public static NameNodeLaunchMode getEnum(String mode) {
		for (NameNodeLaunchMode nameNodeLaunchMode : values()) {
			if (nameNodeLaunchMode.mode.equals(mode)) {
				return nameNodeLaunchMode;
			}
		}
		return SINGLE;
	}
}
