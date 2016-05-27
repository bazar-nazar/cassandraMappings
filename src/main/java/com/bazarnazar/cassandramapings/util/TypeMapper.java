package com.bazarnazar.cassandramapings.util;

import com.datastax.driver.core.TypeCodec;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bazar on 26.05.16.
 */
public final class TypeMapper {

    private TypeMapper() {
    }

    private static final Map<Type, String> TYPES_MAP = new HashMap<>();

    public static String getCQLTypeName(Type type) {
        return TYPES_MAP.get(type);
    }

    static {
        //fixme need to manage default codec
        TypeCodec[] typeCodecs = {
                TypeCodec.blob(),
                TypeCodec.cboolean(),
                TypeCodec.smallInt(),
                TypeCodec.tinyInt(),
                TypeCodec.cint(),
                TypeCodec.bigint(),
                TypeCodec.counter(),
                TypeCodec.cdouble(),
                TypeCodec.cfloat(),
                TypeCodec.varint(),
                TypeCodec.decimal(),
                TypeCodec.ascii(),
                TypeCodec.varchar(),
                TypeCodec.timestamp(),
                TypeCodec.date(),
                TypeCodec.time(),
                TypeCodec.timeUUID(),
                TypeCodec.uuid(),
                TypeCodec.inet()
        };

        Arrays.stream(typeCodecs)
              .forEach(tc -> TYPES_MAP.put(tc.getJavaType().getType(),
                                           tc.getCqlType().getName().toString()));
//        TYPES_MAP.entrySet().forEach((e) -> System.out.println(e.getKey() + ": " + e.getValue()));
    }


}
