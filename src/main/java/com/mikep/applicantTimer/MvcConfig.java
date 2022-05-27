package com.mikep.applicantTimer;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**");
//    }
// /**
//     * We provide a custom configuration which resolves URL-Requests to static files in the
//     * classpath (src/main/resources directory).
//     *
//     * This overloads a default configuration retrieved at least partly from
//     * {@link WebProperties.Resources#getStaticLocations()}.
//     *
//     * @param registry ResourceHandlerRegistry
//     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
//        /*
//         * BE AWARE HERE:
//         *
//         * .addResourceHandler(): URL Paths
//         * .addResourceLocations(): Paths in Classpath to look for file
//         *   root "/" refers to src/main/resources
//         *   For configuration example, see:
//         *     org.springframework.boot.autoconfigure.web.WebProperties.Resources().getStaticLocations()
//         *
//         * .addResourceLocations("classpath:/static/")
//         *   =>
//         *      addResourceHandler("/**")
//         *      => GET /res/css/main.css
//         *         => resolved as: "classpath:/static/res/css/main.css"
//         *      BUT
//         *      addResourceHandler("/res/**")
//         *      => GET /res/css/main.css
//         *            (spring only appends the ** to the value from
//         *             addResourceLocations())
//         *         => resolved as: "classpath:/static/css/main.css"
//         */
//
//        registry
//                .addResourceHandler("/favicon.ico")
//                // trailing slash is important!
//                .addResourceLocations("classpath:/static/")
//                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)
//                        .noTransform()
//                        .mustRevalidate());
//
        registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
        registry.addResourceHandler("/assets/**").addResourceLocations("/", "/assets/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("/", "/webjars");

        registry
                .addResourceHandler("/res/**")
                // trailing slash is important!
                .addResourceLocations("classpath:/static/res/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)
                        .noTransform()
                        .mustRevalidate());

        registry
                .addResourceHandler("/images/**")
                // trailing slash is important!
                .addResourceLocations("classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)
                        .noTransform()
                        .mustRevalidate());
        registry.addResourceHandler("/*.js")
                .addResourceLocations("/frontend/build/");
        registry.addResourceHandler("/*.json")
                .addResourceLocations("/frontend/build/");
        registry.addResourceHandler("/*.ico")
                .addResourceLocations("/frontend/build/");
        registry.addResourceHandler("/index.html")
                .addResourceLocations("/frontend/build/index.html");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true).parameterName("mediaType").ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML).mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("csv", new MediaType("text", "csv"));
    }
}
