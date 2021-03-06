package io.anyway.sherlock.datasource.support.strategy;

import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import io.anyway.sherlock.datasource.DataSourceReadStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import io.anyway.sherlock.datasource.support.WeightDataSourceProxy;
import io.anyway.sherlock.datasource.PartitionDataSource;
import io.anyway.sherlock.datasource.support.PartitionDataSourceSupport;

public class WeightStrategySupport implements DataSourceReadStrategy {

	private Log logger = LogFactory.getLog(getClass());

	@Override
	public DataSource getSlaveDataSource(PartitionDataSource pds) {
		return getDataSourceByWeight(pds,0);
	}

	protected DataSource getDataSourceByWeight(PartitionDataSource pds,int w){
		List<DataSource> slaveDataSources= ((PartitionDataSourceSupport)pds).getSlaveDataSources();
		if(CollectionUtils.isEmpty(slaveDataSources)){
			if(logger.isInfoEnabled()){
				logger.info("SlaveDataSource array of PartitionDataSource ["+pds.getName()+"] is empty, will use MasterDataSource");
			}
			return pds.getMasterDataSource();
		}
		int total= 0;
		//TODO this need cache
		for(DataSource ds: slaveDataSources){
			total+= ((WeightDataSourceProxy)ds).getWeight();
		}
		Random rand = new Random();
		int weight= 0,rdm= rand.nextInt(total+ w);
		for(DataSource ds: slaveDataSources){
			weight+= ((WeightDataSourceProxy)ds).getWeight();
			if(weight> rdm){
				if(logger.isInfoEnabled()){
					logger.info("found SlaveDataSource of PartitionDataSource ["+pds.getName()+"], weight: "+((WeightDataSourceProxy)ds).getWeight());
				}
				return ds;
			}
		}
		return pds.getMasterDataSource();
	}

	@Override
	public String getStrategyName(){
		return "weight";
	}

}