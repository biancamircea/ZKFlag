package ro.mta.toggleserverapi.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import ro.mta.toggleserverapi.entities.Role;
import ro.mta.toggleserverapi.enums.UserRoleType;
import ro.mta.toggleserverapi.exceptions.RoleAlreadyExistsException;
import ro.mta.toggleserverapi.repositories.RoleRepository;

class RoleServiceTest {
    /**
     * Method under test: {@link RoleService#fetchAll()}
     */
    @Test
    void testFetchAll() {
        RoleRepository roleRepository = mock(RoleRepository.class);
        ArrayList<Role> roleList = new ArrayList<>();
        when(roleRepository.findAll()).thenReturn(roleList);
        List<Role> actualFetchAllResult = (new RoleService(roleRepository)).fetchAll();
        assertSame(roleList, actualFetchAllResult);
        assertTrue(actualFetchAllResult.isEmpty());
        verify(roleRepository).findAll();
    }

    /**
     * Method under test: {@link RoleService#fetchAll()}
     */
//    @Test
//    void testFetchAll2() {
//        RoleRepository roleRepository = mock(RoleRepository.class);
//        when(roleRepository.findAll()).thenThrow(new RoleAlreadyExistsException(UserRoleType.ADMIN, "Link"));
//        assertThrows(RoleAlreadyExistsException.class, () -> (new RoleService(roleRepository)).fetchAll());
//        verify(roleRepository).findAll();
//    }
}

