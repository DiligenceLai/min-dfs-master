package com.diligence.dfs.config;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 0:46
 */
public class NameNodeConfig {
	/**
	 * 默认的文件目录
	 */
	private final String DEFAULT_BASEDIR = "/diligence/dfs/namenode";

	/**
	 * 默认监听的端口号
	 */
	private final int DEFAULT_PORT = 2345;
	/**
	 * 默认 EditLog Buffer刷磁盘的阈值
	 */
	private final int DEFAULT_EDITLOG_FLUSH_THRESHOLD = 524288;

	private String baseDir;
	private int port;
	private int editLogFlushThreshold;
}
