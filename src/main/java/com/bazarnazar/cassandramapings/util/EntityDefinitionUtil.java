package com.bazarnazar.cassandramapings.util;

import com.bazarnazar.cassandramapings.annotations.Order;
import com.bazarnazar.cassandramapings.annotations.Static;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Transient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Bazar on 26.05.16.
 */
public final class EntityDefinitionUtil {

    private EntityDefinitionUtil() {
    }

    public static String dropTableQuery(String name) {
        return "DROP TABLE " + name + ";";
    }

    public static String createTableQuery(String name, Map<String, String> columns,
            List<Tuple<String, ClusteringOrder>> partitionKey,
            List<Tuple<String, ClusteringOrder>> clusteringColumns) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("CREATE TABLE ").append(name).append(" (");
        columns.entrySet().forEach(
                e -> queryStringBuilder.append(e.getKey()).append(" ").append(e.getValue())
                                       .append(", "));
        queryStringBuilder.append("PRIMARY KEY (").append(partitionKey.stream().map(t -> t._1)
                                                                      .collect(Collectors.joining(
                                                                              ", ", "(", ")")));
        if (!clusteringColumns.isEmpty()) {
            queryStringBuilder.append(clusteringColumns.stream().map(t -> t._1).collect(
                    Collectors.joining(", ", ", ", "")));
            queryStringBuilder.append(")) WITH CLUSTERING ORDER BY")
                              .append(clusteringColumns.stream().map(t -> t._1 + " " + t._2.name())
                                                       .collect(Collectors.joining(",", "(", ")")))
                              .append(";");
        } else {
            queryStringBuilder.append("));");
        }
        return queryStringBuilder.toString();
    }

    public static List<Tuple<String, ClusteringOrder>> getOrderedItems(Class<?> clazz,
            Class<? extends
                    Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                     .filter(f -> f.getDeclaredAnnotation(annotationClass) != null)
                     .sorted(new ColumnComparator(annotationClass)).map(f -> new Tuple<>(f,
                                                                                         f.getDeclaredAnnotation(
                                                                                                 Order.class) == null ? ClusteringOrder.ASC : f
                                                                                                 .getDeclaredAnnotation(
                                                                                                         Order.class)
                                                                                                 .order()))
                     .map(t -> new Tuple<>(EntityDefinitionUtil.getColumnName(t._1), t._2))
                     .collect(Collectors.toList());
    }

    private static class ColumnComparator implements Comparator<Field> {

        private Class<? extends Annotation> annotationClass;

        public ColumnComparator(Class<? extends Annotation> annotationClass) {
            this.annotationClass = annotationClass;
        }

        @Override
        public int compare(Field o1, Field o2) {
            Class<? extends Annotation> partitionKeyAnnotation = PartitionKey.class;
            if (annotationClass.equals(partitionKeyAnnotation)) {
                return o1.getDeclaredAnnotation(PartitionKey.class).value() - o2
                        .getDeclaredAnnotation(PartitionKey.class).value();
            } else {
                return o1.getDeclaredAnnotation(ClusteringColumn.class).value() - o2
                        .getDeclaredAnnotation(ClusteringColumn.class).value();
            }
        }
    }

    public static void checkStaticColumns(Class<?> clazz, Map<String, String> columns) {
        Arrays.stream(clazz.getDeclaredFields())
              .filter(f -> f.getDeclaredAnnotation(Static.class) != null)
              .map(EntityDefinitionUtil::getColumnName)
              .forEach(n -> columns.computeIfPresent(n, (ket, value) -> value += " static"));
    }

    public static Map<String, String> defineColumns(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                     .filter(f -> f.getDeclaredAnnotation(Transient.class) == null).collect(
                        Collectors.toMap((EntityDefinitionUtil::getColumnName),
                                         (f -> TypeMapper.getCQLTypeName(f.getType()))));
    }


    public static String getColumnName(Field field) {
        if (field.getAnnotation(Column.class) == null || ""
                .equals(field.getAnnotation(Column.class).name())) {
            return field.getName();
        }
        return field.getAnnotation(Column.class).name();
    }

}
