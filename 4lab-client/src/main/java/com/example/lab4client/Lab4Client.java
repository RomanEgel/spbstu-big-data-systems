package com.example.lab4client;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@SpringBootApplication
@EnableZuulProxy
public class Lab4Client {

	public static void main(String[] args) {
		SpringApplication.run(Lab4Client.class, args);
	}

}

@Component
class CustomZuulFilter extends ZuulFilter {

	@Override
	public Object run() throws ZuulException {
		RequestContext currentContext = RequestContext.getCurrentContext();
		currentContext.addZuulRequestHeader("Test", "TestSample");
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return PRE_DECORATION_FILTER_ORDER - 1;
	}
}
