package com.fasterxml.classmate.types;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeBindings;

public final class ResolvedConcreteClass extends ResolvedClass
{

    /*
    /**********************************************************************
    /* Life cycle
    /**********************************************************************
     */

    public ResolvedConcreteClass(Class<?> erased, TypeBindings bindings,
            ResolvedClass superClass, ResolvedType[] interfaces)
    {
        super(erased, bindings, superClass, interfaces);
    }

    /*
    /**********************************************************************
    /* Simple property accessors
    /**********************************************************************
     */

    @Override
    public boolean isConcrete() { return true; }
}
