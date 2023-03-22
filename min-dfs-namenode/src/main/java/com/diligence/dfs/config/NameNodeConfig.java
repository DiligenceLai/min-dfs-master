package com.diligence.dfs.config;

import com.ruyuan.dfs.model.backup.NameNodeConf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author Diligence
 * @create 2023 - 03 - 23 0:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

	public NameNodeConfig(NameNodeConf nameNodeConf) {
		this.baseDir = nameNodeConf.getValuesOrDefault("baseDir", DEFAULT_BASEDIR);
		this.port = Integer.parseInt(nameNodeConf.getValuesOrDefault("port", DEFAULT_PORT + ""));
		this.editLogFlushThreshold = Integer.parseInt(nameNodeConf.getValuesOrDefault("editLogFlushThreshold",
				DEFAULT_EDITLOG_FLUSH_THRESHOLD + ""));
	}

	/**
	 * 获取文件名？
	 * 0~500.log
	 * @param start
	 * @param end
	 * @return
	 */
	public String getEditlogsFile(long start, long end) {
		return baseDir + File.separator + "editslog-" + start + "_" + end + ".log";
	}
}
