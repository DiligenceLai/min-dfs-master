package com.diligence.dfs.namenode.editslog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * editslog文件信息
 * @author Diligence
 * @create 2023 - 03 - 23 0:32
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditslogInfo implements Comparable<EditslogInfo> {
	private long start;
	private long end;
	private String name;

	/**
	 * 自定义排序
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(EditslogInfo o) {
		return (int) (this.start - o.start);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EditslogInfo that = (EditslogInfo) o;
		return start == that.start &&
				end == that.end &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, end, name);
	}
}
