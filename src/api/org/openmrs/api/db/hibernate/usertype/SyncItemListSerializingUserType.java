/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.db.hibernate.usertype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.openmrs.serialization.Item;
import org.openmrs.serialization.Package;
import org.openmrs.serialization.Record;
import org.openmrs.synchronization.engine.SyncItem;

public class SyncItemListSerializingUserType implements UserType {

    private static final int[] SQL_TYPES = {Types.CLOB};
    
    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        //TODO: might need something more detailed here; p206
        return x == y;
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
     */
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        if (rs.wasNull()) {
            return null;
        } else {
            Clob clob = rs.getClob(names[0]);
            
            if (clob == null) {
                return null;
            } else {
                //FIXME: length conversion from long to int might be a problem in theory. UTF8 as well. Better off with the Reader?

            	// 2 Sep 2007 - Christian Allen - callen@pih.org
            	// We need a workaround because clob.getSubString() and clob.length() throw an exception when used within the creating session
                //String content = clob.getSubString(1, (int)clob.length());
            	
            	// Here's the workaround:
            	StringBuilder content = new StringBuilder();
            	String line;

                BufferedReader br = new BufferedReader( rs.getCharacterStream( names[0] ) );
                try {
                	while( (line = br.readLine()) != null ) {
                		content.append(line);
                	}
                } catch (IOException e) {
                	throw new SQLException( e.toString() );
                }
                // End workaround
                
                List<SyncItem> items = new LinkedList<SyncItem>();
                
                Package pkg = new Package();
                try {
                    Record record = pkg.createRecordFromString(content.toString());
                    Item root = record.getRootItem();
                    List<Item> itemsToDeSerialize = record.getItems(root);
                    
                    Iterator<Item> iterator = itemsToDeSerialize.iterator();
                    while(iterator.hasNext()) {
                        SyncItem item = new SyncItem();
                        item.load(record, iterator.next());
                        
                        // Add de-serialized item to list
                        items.add(item);
                    }
                } catch (Exception e) {
                    throw new HibernateException("Could not deserialize object from storage", e);
                }
                
                return items;
            }
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
     */
    @SuppressWarnings("unchecked")
    public void nullSafeSet(PreparedStatement ps, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            ps.setNull(index, Types.CLOB);
        } else {
            List<SyncItem> items = (List<SyncItem>) value;

            org.openmrs.serialization.Package pkg = new Package();
            Record record;
            try {
                record = pkg.createRecordForWrite("items");
                Item root = record.getRootItem();
                
                Iterator<SyncItem> iterator = items.iterator();
                while(iterator.hasNext()) {
                    iterator.next().save(record, root);
                }
            } catch (Exception e) {
                throw new HibernateException("Could not serialize SyncItems", e);
            }
            
            ps.setClob(index, Hibernate.createClob(record.toStringAsDocumentFragement()));
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @SuppressWarnings("unchecked")
    public Class returnedClass() {
        return List.class;
    }

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return SQL_TYPES;
    }
}
