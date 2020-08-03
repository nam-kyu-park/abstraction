package kr.co.sptek.abstraction.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kr.co.sptek.abstraction.properties.DatabaseProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan("kr.co.sptek.abstraction.db.mapper")
@EnableTransactionManagement
public class DatabaseConfig {

    @Autowired
    private DatabaseProperties prop;


    @Bean
    public HikariConfig hikariConfig() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(prop.getDriverClassName());
        hikariConfig.setJdbcUrl(prop.getUrl());
        hikariConfig.setUsername(prop.getUserName());
        hikariConfig.setPassword(prop.getPassword());
        return hikariConfig;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        final DataSource dataSource = new HikariDataSource(hikariConfig());
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource datasource) throws Exception{
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(datasource);
        sessionFactory.setTypeAliasesPackage("kr.co.sptek.abstraction.db.dto");

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml"));
        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory)throws Exception{
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }

}

