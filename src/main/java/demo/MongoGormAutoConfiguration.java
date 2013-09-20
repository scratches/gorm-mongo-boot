package demo;

import grails.persistence.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.grails.datastore.gorm.mongo.config.MongoDatastoreConfigurer;
import org.grails.datastore.mapping.core.Datastore;
import org.grails.datastore.mapping.transactions.DatastoreTransactionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ClassUtils;

@Configuration
@EnableTransactionManagement
@ConditionalOnClass(MongoDatastoreConfigurer.class)
public class MongoGormAutoConfiguration implements BeanFactoryAware, ResourceLoaderAware,
		BeanClassLoaderAware {

	private ClassLoader beanClassLoader;

	private BeanFactory beanFactory;

	private ResourceLoader resourceLoader;

	private Class<?>[] entityClasses() {

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.setResourceLoader(resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class, true, true));

		Set<Class<?>> result = new HashSet<Class<?>>();
		List<String> basePackages = AutoConfigurationUtils
				.getBasePackages(this.beanFactory);

		for (String basePackage : basePackages) {
			Collection<BeanDefinition> components = scanner
					.findCandidateComponents(basePackage);
			for (BeanDefinition definition : components) {
				result.add(ClassUtils.resolveClassName(definition.getBeanClassName(),
						beanClassLoader));
			}
		}
		return result.toArray(new Class<?>[result.size()]);
	}

	@Bean
	public Datastore datastore() {
		return MongoDatastoreConfigurer.configure("test", entityClasses());
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DatastoreTransactionManager transactionManager = new DatastoreTransactionManager();
		transactionManager.setDatastore(datastore());
		return transactionManager;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		beanClassLoader = classLoader;
	}

}
