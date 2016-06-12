package net.xicp.hkscript.gateway.gateway.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.RegistryService;

public class RegistryServerSync implements InitializingBean, DisposableBean, NotifyListener {

    private Logger logger = Logger.getLogger(RegistryServerSync.class);
    @Autowired
    private RegistryService registryService;

    // private static final URL SUBSCRIBE = new URL(Constants.ADMIN_PROTOCOL,
    // NetUtils.getLocalHost(), 0, "",
    // Constants.INTERFACE_KEY, Constants.ANY_VALUE,
    // Constants.GROUP_KEY, Constants.ANY_VALUE,
    // Constants.VERSION_KEY, Constants.ANY_VALUE,
    // Constants.CLASSIFIER_KEY, Constants.ANY_VALUE,
    // Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY + ","
    // + Constants.CONSUMERS_CATEGORY + ","
    // + Constants.ROUTERS_CATEGORY + ","
    // + Constants.CONFIGURATORS_CATEGORY,
    // Constants.ENABLED_KEY, Constants.ANY_VALUE,
    // Constants.CHECK_KEY, String.valueOf(false));

    private static final URL SUBSCRIBE = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "",
            Constants.INTERFACE_KEY, "*", Constants.GROUP_KEY, Constants.ANY_VALUE, Constants.VERSION_KEY,
            Constants.ANY_VALUE, Constants.CLASSIFIER_KEY, Constants.ANY_VALUE, Constants.CATEGORY_KEY,
            Constants.PROVIDERS_CATEGORY, Constants.ENABLED_KEY, Constants.ANY_VALUE, Constants.CHECK_KEY,
            String.valueOf(false));

    private final ConcurrentMap<String, ConcurrentMap<String, Map<Long, URL>>> registryCache = new ConcurrentHashMap<String, ConcurrentMap<String, Map<Long, URL>>>();

    private static final AtomicLong ID = new AtomicLong();

    public ConcurrentMap<String, ConcurrentMap<String, Map<Long, URL>>> getRegistryCache() {
        return registryCache;
    }

    public List<URL> getServices() {
        List<URL> services = new ArrayList();
        ConcurrentMap<String, Map<Long, URL>> providerUrls = getRegistryCache().get(Constants.PROVIDERS_CATEGORY);
        if (providerUrls != null) {
            Set<Entry<String, Map<Long, URL>>> entries = providerUrls.entrySet();
            for (Entry<String, Map<Long, URL>> entry : entries) {
                String key = entry.getKey();


                Collection<URL> groupUrls = entry.getValue().values();
                for (URL url : groupUrls) {
                    services.add(url);
                    break;
                }

            }
        }
    

        return services;

    }

    @Override
    public void notify(List<URL> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        // Map<category, Map<servicename, Map<Long, URL>>>
        final Map<String, Map<String, Map<Long, URL>>> categories = new HashMap<String, Map<String, Map<Long, URL>>>();
        for (URL url : urls) {
            String category = url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS_CATEGORY);
            if (Constants.EMPTY_PROTOCOL.equalsIgnoreCase(url.getProtocol())) { // 注意：empty协议的group和version为*
                ConcurrentMap<String, Map<Long, URL>> services = registryCache.get(category);
                if (services != null) {
                    String group = url.getParameter(Constants.GROUP_KEY);
                    String version = url.getParameter(Constants.VERSION_KEY);
                    // 注意：empty协议的group和version为*
                    if (!Constants.ANY_VALUE.equals(group) && !Constants.ANY_VALUE.equals(version)) {
                        services.remove(url.getServiceKey());
                    } else {
                        for (Map.Entry<String, Map<Long, URL>> serviceEntry : services.entrySet()) {
                            String service = serviceEntry.getKey();
                            if (Tool.getInterface(service).equals(url.getServiceInterface())
                                    && (Constants.ANY_VALUE.equals(group)
                                            || StringUtils.isEquals(group, Tool.getGroup(service)))
                                    && (Constants.ANY_VALUE.equals(version)
                                            || StringUtils.isEquals(version, Tool.getVersion(service)))) {
                                services.remove(service);
                            }
                        }
                    }
                }
            } else {
                Map<String, Map<Long, URL>> services = categories.get(category);
                if (services == null) {
                    services = new HashMap<String, Map<Long, URL>>();
                    categories.put(category, services);
                }
                String service = url.getServiceKey();
                Map<Long, URL> ids = services.get(service);
                if (ids == null) {
                    ids = new HashMap<Long, URL>();
                    services.put(service, ids);
                }
                ids.put(ID.incrementAndGet(), url);
            }
        }
        for (Map.Entry<String, Map<String, Map<Long, URL>>> categoryEntry : categories.entrySet()) {
            String category = categoryEntry.getKey();
            ConcurrentMap<String, Map<Long, URL>> services = registryCache.get(category);
            if (services == null) {
                services = new ConcurrentHashMap<String, Map<Long, URL>>();
                registryCache.put(category, services);
            }
            services.putAll(categoryEntry.getValue());
        }

    }

    @Override
    public void destroy() throws Exception {
        registryService.unsubscribe(SUBSCRIBE, this);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Init Dubbo Admin Sync Cache...");
        registryService.subscribe(SUBSCRIBE, this);

    }

}
