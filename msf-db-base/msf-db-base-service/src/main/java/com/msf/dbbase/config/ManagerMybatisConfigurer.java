package com.msf.dbbase.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.github.pagehelper.PageInterceptor;
import com.msf.dbbase.core.YsappMapper;
import com.msf.dbbase.plugins.ExecutorQueryInterceptor;
import com.msf.dbbase.plugins.ExecutorUpdateInterceptor;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/**
 * Mybatis & Mapper  配置
 */
@Configuration
public class ManagerMybatisConfigurer {
	
	private String aopPointcutExpression = "execution (* com.msf.dbbase.service.impl.*.*(..))";
	
	private String modelPackage = "com.msf.dbbase.entity";
	
	private String mapperPackage = "com.msf.dbbase.mapper";
	
	@Value("${mybatis.mapper-locations}")
	private String mapperLocations;
	
	private String mapperIdentity = "MYSQL";

	@Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("druidDataSource") DataSource dataSource) {
     return new DataSourceTransactionManager(dataSource);
    }
    
   // 事务拦截器
    @Bean("txInterceptor")
    TransactionInterceptor getTransactionInterceptor(@Qualifier("transactionManager") PlatformTransactionManager tx){
        return new TransactionInterceptor(tx , transactionAttributeSource()) ;
    }
    
    /**切面拦截规则 参数会自动从容器中注入*/
    @Bean
    public AspectJExpressionPointcutAdvisor pointcutAdvisor(@Qualifier("txInterceptor")TransactionInterceptor txInterceptor){
        AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setAdvice(txInterceptor);
        pointcutAdvisor.setExpression(aopPointcutExpression);
        return pointcutAdvisor;
    }
    
	@Bean(name="sqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("druidDataSource") DataSource dataSource, ExecutorQueryInterceptor executorQueryInterceptor, ExecutorUpdateInterceptor executorUpdateInterceptor) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage(modelPackage);


        //配置分页插件，详情请查阅官方文档
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "true");//分页尺寸为0时查询所有纪录不再执行分页
        properties.setProperty("reasonable", "true");//页码<=0 查询第一页，页码>=总页数查询最后一页
        properties.setProperty("supportMethodsArguments", "true");//支持通过 Mapper 接口参数来传递分页参数
        interceptor.setProperties(properties);
        
        //添加插件
        factory.setPlugins(new Interceptor[]{interceptor,executorQueryInterceptor,executorUpdateInterceptor});

        //添加XML目录
		/*
		 * ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		 * factory.setMapperLocations(resolver.getResources(mapperLocations));
		 */
        return factory.getObject();
    }
	
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactoryBean") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("txSource")
    public TransactionAttributeSource transactionAttributeSource(){
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        // 只读事务，不做更新操作
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED );
        //当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务
        //RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        //requiredTx.setRollbackRules(
        //    Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        //requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
            Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setTimeout(20);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("get*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        source.setNameMap( txMap );
        return source;
    }
    
    /**
     * MapperScannerConfigurer实现过BeanFactoryPostProcessor，最开始实例化时会出现比如无法获取到application.properties
     * @return
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage(mapperPackage);
        mapperScannerConfigurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");

        //配置通用Mapper，详情请查阅官方文档
        Properties properties = new Properties();
        properties.setProperty("mappers", YsappMapper.class.getName());
        properties.setProperty("notEmpty", "false");//insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
        properties.setProperty("IDENTITY", mapperIdentity);
        mapperScannerConfigurer.setProperties(properties);

        return mapperScannerConfigurer;
    }
}

