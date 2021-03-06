/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class DiscussionTopic extends KnowledgeArtifact {

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private OffsetDateTime date;

    @ManyToOne(fetch = FetchType.LAZY,
               optional = false)
    @EqualsAndHashCode.Exclude
    private KnowledgeArtifact knowledgeArtifact;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "discussionTopic",
               orphanRemoval = true,
               cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Set<DiscussionComment> discussionComments = new HashSet<>();
}
