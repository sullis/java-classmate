package com.fasterxml.classmate;

import java.util.*;

public abstract class ResolvedType
{
    protected final static ResolvedType[] NO_TYPES = new ResolvedType[0];
    
    protected final Class<?> _erasedType;

    /**
     * Type bindings active when resolving members (methods, fields,
     * constructors) of this type
     */
    protected final TypeBindings _typeBindings;
    
    /*
    /**********************************************************************
    /* Life cycle
    /**********************************************************************
     */
    
    protected ResolvedType(Class<?> cls, TypeBindings bindings)
    {
        _erasedType = cls;
        _typeBindings = (bindings == null) ? TypeBindings.emptyBindings() : bindings;
    }

    /*
    /**********************************************************************
    /* Accessors for related types
    /**********************************************************************
     */
    
    public Class<?> getErasedType() { return _erasedType; }

    public abstract ResolvedType getParentClass();

    /**
     * Method that can be used to access element type of array types; will return
     * null for non-array types, and non-null type for array types.
     */
    public abstract ResolvedType getArrayElementType();
    
    public abstract List<ResolvedType> getImplementedInterfaces();

    public List<ResolvedType> getTypeParameters() {
        return _typeBindings.getTypeParameters();
    }

    /**
     * Method for accessing bindings of type variables to resolved types in context
     * of this type.
     */
    public TypeBindings getBindings() { return _typeBindings; }
    
    /**
     * Method that will try to find type parameterization this type
     * has for specified super type
     * 
     * @return List of type parameters for specified supertype (which may
     *   be empty, if supertype is not a parametric type); null if specified
     *   type is not a super type of this type
     */
    public List<ResolvedType> typeParametersFor(Class<?> erasedSupertype)
    {
        ResolvedType type = findSupertype(erasedSupertype);
        if (type != null) {
            return type.getTypeParameters();
        }
        // nope; doesn't look like we extend or implement super type in question
        return null;
    }

    public ResolvedType findSupertype(Class<?> erasedSupertype)
    {
        if (erasedSupertype == _erasedType) {
            return this;
        }
        ResolvedType pc = getParentClass();
        if (pc != null) {
            ResolvedType type = pc.findSupertype(erasedSupertype);
            if (type != null) {
                return type;
            }
        }
        // if not found, and we are looking for an interface, try implemented interfaces:
        if (erasedSupertype.isInterface()) {
            for (ResolvedType it : getImplementedInterfaces()) {
                ResolvedType type = it.findSupertype(erasedSupertype);
                if (type != null) {
                    return type;
                }
            }
        }
        // nope; doesn't look like we extend or implement super type in question
        return null;
    }
    
    /*
    /**********************************************************************
    /* Simple property accessors
    /**********************************************************************
     */
    
    public abstract boolean isInterface();
    public abstract boolean isConcrete();
    public final boolean isAbstract() { return !isConcrete(); }

    /**
     * Method that indicates whether this type is an array type.
     */
    public abstract boolean isArray();

    /**
     * Method that indicates whether this type is one of small number of primitive
     * Java types; not including array types of primitive types but just basic
     * primitive types.
     */
    public abstract boolean isPrimitive();

    public final boolean isInstanceOf(Class<?> type) {
        return type.isAssignableFrom(_erasedType);
    }
    
    /*
    /**********************************************************************
    /* String representations
    /**********************************************************************
     */

    /**
     * Method that returns full generic signature of the type; suitable
     * as signature for things like ASM package.
     */
    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        return appendSignature(sb).toString();
    }

    /**
     * Method that returns type erased signature of the type; suitable
     * as non-generic signature some packages need
     */
    public String getErasedSignature() {
        StringBuilder sb = new StringBuilder();
        return appendErasedSignature(sb).toString();
    }

    /**
     * Human-readable description of type
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        return appendFullDescription(sb).toString();
    }

    public abstract StringBuilder appendBriefDescription(StringBuilder sb);
    public abstract StringBuilder appendFullDescription(StringBuilder sb);
    public abstract StringBuilder appendSignature(StringBuilder sb);
    public abstract StringBuilder appendErasedSignature(StringBuilder sb);

    /*
    /**********************************************************************
    /* Helper methods for sub-classes
    /**********************************************************************
     */

    protected StringBuilder _appendClassSignature(StringBuilder sb)
    {
        sb.append('L');
        sb = _appendClassName(sb);
        int count = _typeBindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                sb = _typeBindings.getBoundType(i).appendErasedSignature(sb);
            }
            sb.append('>');
        }
        sb.append(';');
        return sb;
    }

    protected StringBuilder _appendErasedClassSignature(StringBuilder sb)
    {
        sb.append('L');
        sb = _appendClassName(sb);
        sb.append(';');
        return sb;
    }

    protected StringBuilder _appendClassDescription(StringBuilder sb)
    {
        sb.append(_erasedType.getName());
        int count = _typeBindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb = _typeBindings.getBoundType(i).appendBriefDescription(sb);
            }
            sb.append('>');
        }
        return sb;
    }
    
    protected StringBuilder _appendClassName(StringBuilder sb)
    {
        String name = _erasedType.getName();
        for (int i = 0, len = name.length(); i < len; ++i) {
            char c = name.charAt(i);
            if (c == '.') c = '/';
            sb.append(c);
        }
        return sb;
    }
}
