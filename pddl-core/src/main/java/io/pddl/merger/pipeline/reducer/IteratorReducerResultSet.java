/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.pddl.merger.pipeline.reducer;

import io.pddl.merger.MergeContext;
import io.pddl.merger.resultset.delegate.AbstractDelegateResultSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 迭代归并的聚集结果集.
 *
 * @author gaohongtao
 * @author zhangliang
 * @author xiong.j
 */
public final class IteratorReducerResultSet extends AbstractDelegateResultSet {
    
    private int resultSetIndex = 1;
    
    public IteratorReducerResultSet(final MergeContext mc) throws SQLException {
        super(mc.getResultSets());
    }
    
    @Override
    protected boolean firstNext() throws SQLException {
        return processCurrent()|| (!isOutOfIndex() && processNext());
    }
    
    @Override
    protected boolean afterFirstNext() throws SQLException {
        return processCurrent() || (!isOutOfIndex() && processNext());
    }
    
    private boolean processCurrent() throws SQLException {
        return getDelegate().next();
    }
    
    private boolean isOutOfIndex() {
        return resultSetIndex >= getResultSets().size();
    }
    
    private boolean processNext() throws SQLException {
        ResultSet resultSet = getResultSets().get(resultSetIndex++);
        setDelegate(resultSet);
        return resultSet.next();
    }
}
