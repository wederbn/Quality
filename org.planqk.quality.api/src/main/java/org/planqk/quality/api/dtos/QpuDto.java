/*
 *  /*******************************************************************************
 *  * Copyright (c) 2020 University of Stuttgart
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 */

package org.planqk.quality.api.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.planqk.quality.model.HasId;
import org.planqk.quality.model.Provider;
import org.planqk.quality.model.Qpu;
import org.planqk.quality.model.Sdk;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for the model class {@link Qpu}.
 */
public class QpuDto extends RepresentationModel<ProviderDto> {

    private Long id;

    private String name;

    private int numberOfQubits;

    private List<Long> supportedSdkIds;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNumberOfQubits(int numberOfQubits) {
        this.numberOfQubits = numberOfQubits;
    }

    public int getNumberOfQubits() {
        return numberOfQubits;
    }

    public List<Long> getSupportedSdkIds() {
        return supportedSdkIds;
    }

    public void setSupportedSdkIds(List<Long> supportedSdkIds) {
        this.supportedSdkIds = supportedSdkIds;
    }

    public static final class Converter {

        public static QpuDto convert(final Qpu object) {
            QpuDto dto = new QpuDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setNumberOfQubits(object.getQubitCount());
            dto.setSupportedSdkIds(object.getSupportedSdks().stream().map(HasId::getId).collect(Collectors.toList()));
            return dto;
        }

        public static Qpu convert(final QpuDto object, final Provider provider, final List<Sdk> supportedSdks) {
            Qpu qpu = new Qpu();
            qpu.setName(object.getName());
            qpu.setQubitCount(object.getNumberOfQubits());
            qpu.setProvider(provider);
            qpu.setSupportedSdks(supportedSdks);
            return qpu;
        }
    }
}
