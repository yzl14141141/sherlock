package io.pddl.datasource.support.strategy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;

import io.pddl.datasource.PartitionDataSource;
import io.pddl.datasource.DatabaseReadStrategy;
import io.pddl.datasource.support.PartitionDataSourceSupport;

public class PollingReadStrategySupport implements DatabaseReadStrategy{
	
	private ConcurrentHashMap<String,AtomicLong> hash= new ConcurrentHashMap<String,AtomicLong>();

	@Override
	public DataSource getSlaveDataSource(PartitionDataSource ds) {
		return getDataSourceByPolling(ds,0);
	}
	
	protected DataSource getDataSourceByPolling(PartitionDataSource ds,int w){
		List<DataSource> readDataSources= ((PartitionDataSourceSupport)ds).getReadDataSources();
		if(CollectionUtils.isEmpty(readDataSources)){
			return ds.getMasterDataSource();
		}
		AtomicLong next= hash.get(ds.getName());
		if(next== null){
			hash.putIfAbsent(ds.getName(), new AtomicLong(0));
			if((next= hash.get(ds.getName()))==null){
				return getDataSourceByPolling(ds,w);
			}
		}
    	int total= readDataSources.size()+ w,
        	idx= (int)(next.getAndIncrement() % total);
        return idx< readDataSources.size()? readDataSources.get(idx): ds.getMasterDataSource();
    }
	
	@Override
	public String getStrategyName(){
		return "polling";
	}

}