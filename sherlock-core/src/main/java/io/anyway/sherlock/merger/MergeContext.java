package io.anyway.sherlock.merger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import io.anyway.sherlock.executor.ExecuteContext;
import io.anyway.sherlock.sqlparser.bean.AggregationColumn;
import io.anyway.sherlock.sqlparser.bean.GroupColumn;
import io.anyway.sherlock.sqlparser.bean.OrderColumn;
import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.ToString;

/**
 * 结果集归并上下文.
 *
 * @author xiong.j
 */
@Getter
@ToString
public final class MergeContext {
    
    private final List<ResultSet> resultSets;
    
    private final ExecuteContext ctx;

    public MergeContext(final List<ResultSet> resultSets, final ExecuteContext ctx) throws SQLException {
        this.resultSets = resultSets;
        this.ctx = ctx;
    }
    
    public List<OrderColumn> getOrderColumns() {
        if (hasOrderColumn()) {
            return ctx.getSQLParsedResult().getOrderColumns();
        }
        return null;
    }

    public boolean hasOrderColumn() {
        return !CollectionUtils.isEmpty(ctx.getSQLParsedResult().getOrderColumns());
    }

    public List<GroupColumn> getGroupColumns() {
        if (hasGroupColumn()) {
            return ctx.getSQLParsedResult().getGroupColumns();
        }
        return Collections.emptyList();
    }

    public boolean hasGroupColumn() {
        return !CollectionUtils.isEmpty(ctx.getSQLParsedResult().getGroupColumns());
    }

    public boolean hasAggregationColumn() {
        return !CollectionUtils.isEmpty(ctx.getSQLParsedResult().getAggregationColumns());
    }

    public List<AggregationColumn> getAgregationColumns() {
        if (hasAggregationColumn()) {
            return ctx.getSQLParsedResult().getAggregationColumns();
        }
        return Collections.emptyList();
    }

    public Limit getLimit() {
        return ctx.getSQLParsedResult()!=null?ctx.getSQLParsedResult().getLimit(): null;
    }

    /**
     * 判断是否有限定结果集计算.
     *
     * @return true:是限定结果集计算 false:不是限定结果集计算
     */
    public boolean hasLimit() {
        return null != getLimit();
    }


    /**
     * 判断是否为分组或者聚合计算.
     * 此处将聚合计算想象成为特殊的分组计算,统一进行处理.
     *
     * @return true:是分组或者聚合计算 false:不是分组且不是聚合计算
     */
    public boolean hasGroupByOrAggregation() {
        return hasGroupColumn() || hasAggregationColumn();
    }


    /**
     * 判断排序归并是否需要内存排序.
     *
     * @return 排序归并是否需要内存排序
     */
    public boolean isNeedMemorySortForOrderBy() {
        return hasOrderColumn() && hasGroupColumn();
    }
}
