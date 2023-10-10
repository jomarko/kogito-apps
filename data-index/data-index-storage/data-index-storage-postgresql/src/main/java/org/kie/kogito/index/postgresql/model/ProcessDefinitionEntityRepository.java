package org.kie.kogito.index.postgresql.model;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class ProcessDefinitionEntityRepository implements PanacheRepositoryBase<ProcessDefinitionEntity, ProcessDefinitionEntityId> {

}