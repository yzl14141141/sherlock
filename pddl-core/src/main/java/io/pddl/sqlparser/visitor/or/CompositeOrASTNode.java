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

package io.pddl.sqlparser.visitor.or;

import java.util.ArrayList;
import java.util.List;

import io.pddl.sqlparser.bean.Condition;
import io.pddl.sqlparser.bean.ConditionContext;

/**
 * 存在外层条件的节点.
 * 
 */
public class CompositeOrASTNode extends AbstractOrASTNode {
    
    private final List<Condition> outConditions = new ArrayList<Condition>();
    
    public void addOutConditions(final ConditionContext outConditions) {
        this.outConditions.addAll(outConditions.getAllCondition());
    }
    
    @Override
    public void createOrASTAsRootNode() {
        for (AbstractOrASTNode each : getSubNodes()) {
            each.createOrASTAsRootNode();
        }
        mergeSubConditions();
        for (List<Condition> each : getNestedConditions()) {
            each.addAll(outConditions);
        }
    }
}
