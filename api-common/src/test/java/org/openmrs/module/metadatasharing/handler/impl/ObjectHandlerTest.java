package org.openmrs.module.metadatasharing.handler.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.OpenmrsObject;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.module.metadatasharing.ImportType;
import org.openmrs.module.metadatasharing.MetadataSharing;
import org.openmrs.module.metadatasharing.handler.Handler;
import org.openmrs.module.metadatasharing.handler.HandlerEngine;
import org.openmrs.module.metadatasharing.merger.ComparisonEngine;
import org.openmrs.module.metadatasharing.visitor.impl.OpenmrsObjectVisitor;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetadataSharing.class, Handler.class })
public class ObjectHandlerTest {

    @Test
    public void testMerge() throws Exception {
//        MetadataSharing mds = Mockito.mock(MetadataSharing.class);
//
//        PowerMockito.mockStatic(MetadataSharing.class);
//        when(MetadataSharing.getInstance()).thenReturn(mds);

        PowerMockito.mockStatic(Handler.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        });

        Privilege p1 = buildPrivilege("Privilege 1", "A privilege");
        Privilege p2 = buildPrivilege("Privilege 2", "A privilege");
        Privilege p3 = buildPrivilege("Privilege 3", "A privilege");
        Privilege p4 = buildPrivilege("Privilege 4", "A privilege");

        Role existing = new Role();
        existing.addPrivilege(p2);
        existing.addPrivilege(p3);

        Role incoming = new Role();
        incoming.addPrivilege(p1);
        incoming.addPrivilege(p2);
        incoming.addPrivilege(p3);
        incoming.addPrivilege(p4);

        Map<Object, Object> incomingToExisting = new HashMap<Object, Object>();
        incomingToExisting.put(p1, p1);
        incomingToExisting.put(p2, p2);
        incomingToExisting.put(p3, p3);
        incomingToExisting.put(p4, p4);

        ObjectHandler objectHandler = new ObjectHandler(new OpenmrsObjectVisitor(), new ComparisonEngine());
        objectHandler.merge(existing, incoming, ImportType.OVERWRITE_MINE, incomingToExisting);

        assertThat(existing.getPrivileges().size(), is(4));
        assertThat(existing.getPrivileges(), containsInAnyOrder(p1, p2, p3, p4));
    }

    private Privilege buildPrivilege(String name, String description) {
        Privilege privilege = new Privilege();
        privilege.setUuid(UUID.randomUUID().toString());
        privilege.setPrivilege(name);
        privilege.setDescription(description);
        return privilege;
    }

}
