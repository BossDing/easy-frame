package com.frame.easy.common.table;

/**
 * 表格标题
 *
 * @author tengchong
 * @date 2019-04-21
 */
public class Column {
    /**
     * 字段名称
     */
    private String field;
    /**
     * 标题
     */
    private String title;
    /**
     * 字典类型
     */
    private String dictType;
    /**
     * 排序方式(desc/asc)
     */
    private String sortable;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getSortable() {
        return sortable;
    }

    public void setSortable(String sortable) {
        this.sortable = sortable;
    }
}
