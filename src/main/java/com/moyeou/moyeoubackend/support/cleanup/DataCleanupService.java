package com.moyeou.moyeoubackend.support.cleanup;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("demo")
@Service
@RequiredArgsConstructor
public class DataCleanupService {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .map(this::extractTableName)
                .collect(Collectors.toList());
    }

    private String extractTableName(EntityType<?> entity) {
        Table declaredAnnotation = entity.getJavaType().getDeclaredAnnotation(Table.class);
        if (declaredAnnotation == null) {
            return convertCamelToSnake(entity.getName());
        }
        return declaredAnnotation.name();
    }

    private String convertCamelToSnake(String str){
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        return str.replaceAll(regex, replacement).toLowerCase();
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                    .executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
