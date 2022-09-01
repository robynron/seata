/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.metrics.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.seata.common.loader.EnhancedServiceLoader;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.seata.metrics.IdConstants.ROLE_VALUE_SERVER;

/**
 * Exporter Factory for load all configured exporters
 *
 * @author zhengyangyong
 */
public class ExporterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExporterFactory.class);

    public static List<Exporter> getInstanceList(String role) {
        String exporterTypeNameList = role.equals(ROLE_VALUE_SERVER) ? ConfigurationFactory.getInstance().getConfig(
                ConfigurationKeys.SERVER_METRICS_PREFIX + ConfigurationKeys.METRICS_EXPORTER_LIST, "prometheus") :
                ConfigurationFactory.getInstance().getConfig(
                        ConfigurationKeys.CLIENT_METRICS_PREFIX + ConfigurationKeys.METRICS_EXPORTER_LIST, "prometheus");
        List<Exporter> exporters = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(exporterTypeNameList)) {
            String[] exporterTypeNames = exporterTypeNameList.split(",");
            for (String exporterTypeName : exporterTypeNames) {
                ExporterType exporterType;
                try {
                    exporterType = ExporterType.getType(exporterTypeName);
                    exporters.add(
                        EnhancedServiceLoader.load(Exporter.class, Objects.requireNonNull(exporterType).getName(), new String[]{role}));
                } catch (Exception exx) {
                    LOGGER.error("not support metrics exporter type: {}",exporterTypeName, exx);
                }
            }
        }
        return exporters;
    }
}
