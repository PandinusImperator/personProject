package com.wangwd.platform.configuration;

//import com.sun.java.util.jar.pack.Attribute;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import org.springframework.util.MimeType;
import javax.annotation.PostConstruct;
import javax.servlet.Servlet;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
public class ThymeleafConfig {

    private static final Log logger = LogFactory.getLog( ThymeleafConfig.class );

    @Configuration
    @ConditionalOnMissingBean(name = "defaultTemplateResolver")
    public static class DefaultTemplateResolverConfiguration {

        @Autowired
        private ThymeleafProperties properties;

        @Autowired
        private ApplicationContext applicationContext;

        @PostConstruct
        public void checkTemplateLocationExists() {
            boolean checkTemplateLocation = this.properties.isCheckTemplateLocation();
            if ( checkTemplateLocation ) {
                TemplateLocation location = new TemplateLocation( this.properties.getPrefix() );
                if ( !location.exists( this.applicationContext ) ) {
                    logger.warn( "Cannot find template location：" + location
                            + "（please add some templates or check "
                            + "your Thymeleaf configuration）");
                }
            }
        }

        @Bean
        public ITemplateResolver defaultTemplateResolver() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext( applicationContext );
            resolver.setPrefix( properties.getPrefix() );
            resolver.setSuffix( properties.getSuffix() );
            resolver.setTemplateMode( properties.getMode() );
            if ( null != properties.getEncoding()) {
                resolver.setCharacterEncoding( properties.getEncoding().name() );
            }
            resolver.setCacheable( properties.isCache() );
            if ( null != properties.getTemplateResolverOrder() ) {
                resolver.setOrder( properties.getTemplateResolverOrder() );
            }
            return resolver;
        }
    }

    @Configuration
    @ConditionalOnMissingBean(SpringTemplateEngine.class)
    protected static class ThymeleafDefaultConfiguration {

        @Autowired
        private final Collection<ITemplateResolver> templateResolvers = Collections.emptySet();

        @Autowired(required = false)
        private final Collection<IDialect> dialects = Collections.emptySet();

        @Bean
        public SpringTemplateEngine templateEngine() {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            for ( ITemplateResolver templateResolver : this.templateResolvers ) {
                engine.addTemplateResolver( templateResolver );
            }
            for ( IDialect dialect : this.dialects ) {
                engine.addDialect( dialect );
            }
            return engine;
        }

    }

    @Configuration
    @ConditionalOnClass(name = "nz.net.ultraq.thymeleaf.LayoutDialect")
    protected static class ThymeleafWebLayoutConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public LayoutDialect layoutDialect() {
            return new LayoutDialect();
        }

    }

    @Configuration
    @ConditionalOnClass({ Servlet.class})
    @ConditionalOnWebApplication
    protected static class ThymeleafViewResolverConfiguration {

        @Autowired
        private ThymeleafProperties properties;

        @Autowired
        private SpringTemplateEngine templateEngine;

        @Bean
        @ConditionalOnMissingBean(name = "thymeleafViewResolver")
        @ConditionalOnProperty(name = "spring.thymeleaf.enablead", matchIfMissing = true)
        public ThymeleafViewResolver thymeleafViewResolver() {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine( this.templateEngine );
            resolver.setCharacterEncoding( this.properties.getEncoding().name() );
            resolver.setContentType( appendCharset( this.properties.getServlet().getContentType(), resolver.getCharacterEncoding() ) );
            resolver.setExcludedViewNames( this.properties.getExcludedViewNames() );
            resolver.setViewNames( this.properties.getViewNames() );
            resolver.setOrder( Ordered.LOWEST_PRECEDENCE - 5 );
            resolver.setCache( this.properties.isCache() );

            return resolver;
        }


        private String appendCharset( MimeType type, String charset ) {
            if ( null != type.getCharset() ) {
                return type.toString();
            }
            LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
            parameters.put("charset", charset);
            parameters.putAll(type.getParameters());
            return new MimeType(type, parameters).toString();
        }
    }

    @Configuration
    @ConditionalOnWebApplication
    protected static class ThymeleafResourceHandlingConfig {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnEnabledResourceChain
        public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
            return new ResourceUrlEncodingFilter();
        }

    }
}
